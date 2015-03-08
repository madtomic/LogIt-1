/*
 * Copyright (C) 2012-2015 LucasEasedUp & NorthPL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package io.github.lucaseasedup.logit.backup;

import static io.github.lucaseasedup.logit.message.MessageHelper.t;
import io.github.lucaseasedup.logit.account.AccountManager;
import io.github.lucaseasedup.logit.bukkit.LogItCoreObject;
import io.github.lucaseasedup.logit.common.Timer;
import io.github.lucaseasedup.logit.config.TimeUnit;
import io.github.lucaseasedup.logit.storage.SqliteStorage;
import io.github.lucaseasedup.logit.storage.Storage;
import io.github.lucaseasedup.logit.storage.Storage.DataType;
import io.github.lucaseasedup.logit.util.ExceptionHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.logging.Level;

import io.github.lucaseasedup.logit.util.org.apache.commons.lang3.StringUtils;

import org.bukkit.scheduler.BukkitRunnable;

public final class BackupManager extends LogItCoreObject implements Runnable
{
	/**
	 * Creates a new {@code BackupManager}.
	 * 
	 * @param accountManager
	 *            the {@code AccountManager} that this {@code BackupManager} will operate on.
	 * @throws IllegalArgumentException
	 *             if {@code accountManager} is {@code null}.
	 */
	public BackupManager(AccountManager accountManager)
	{
		if (accountManager == null)
			throw new IllegalArgumentException();

		timer = new Timer(TASK_PERIOD);
		timer.start();

		this.accountManager = accountManager;
	}

	@Override
	public void dispose()
	{
		timer = null;
		accountManager = null;
	}

	/**
	 * Internal method. Do not call directly.
	 */
	@Override
	public void run()
	{
		if (!getConfig("config.yml").getBoolean("backup.schedule.enabled"))
			return;

		timer.run();

		long interval = getConfig("config.yml").getTime(
				"backup.schedule.interval", TimeUnit.TICKS);

		if (timer.getElapsed() >= interval)
		{
			createBackupAsynchronously();

			timer.reset();
		}
	}

	/**
	 * Creates a new backup of all the accounts stored
	 * in the underlying {@code AccountManager}.
	 * <p>
	 * If this method was called with the {@code asynchronously} parameter set to {@code true}.
	 * 
	 * @return the created backup file.
	 */
	public File createBackup()
	{
		final File backupFile = allocateBackupFileForDate(new Date());

		log(Level.INFO, t("createBackup.creating"));

		try
		{
			exportAccounts(backupFile);

			log(Level.INFO,
					t("createBackup.success.log").replace("{0}",
							backupFile.getName()));
		}
		catch (IOException ex)
		{
			log(Level.WARNING, t("createBackup.fail.log"), ex);

			ExceptionHandler.handleException(ex);
		}

		return backupFile;
	}

	/**
	 * Asynchronously creates a new backup of all the accounts stored
	 * in the underlying {@code AccountManager}.
	 * <p>
	 * If this method was called with the {@code asynchronously} parameter set to {@code true}.
	 * 
	 * @return the created backup file.
	 */
	public File createBackupAsynchronously()
	{
		final File backupFile = allocateBackupFileForDate(new Date());

		log(Level.INFO, t("createBackup.creating"));

		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				try
				{
					exportAccounts(backupFile);

					log(Level.INFO,
							t("createBackup.success.log").replace("{0}",
									backupFile.getName()));
				}
				catch (IOException ex)
				{
					log(Level.WARNING, t("createBackup.fail.log"), ex);
				}
			}
		}.runTaskAsynchronously(getPlugin());

		return backupFile;
	}

	private void exportAccounts(File backupFile) throws IOException
	{
		if (backupFile == null)
			throw new IllegalArgumentException();

		Hashtable<String, DataType> keys = accountManager.getStorage().getKeys(
				accountManager.getUnit());
		String primaryKey = accountManager.getStorage().getPrimaryKey(
				accountManager.getUnit());
		List<Storage.Entry> entries = accountManager.getStorage()
				.selectEntries(accountManager.getUnit());

		try (Storage backupStorage = new SqliteStorage("jdbc:sqlite:"
				+ backupFile))
		{
			backupStorage.connect();
			backupStorage.createUnit("accounts", keys, primaryKey);

			try
			{
				backupStorage.setAutobatchEnabled(true);

				for (Storage.Entry entry : entries)
				{
					backupStorage.addEntry("accounts", entry);
				}

				backupStorage.executeBatch();
				backupStorage.clearBatch();
			}
			finally
			{
				backupStorage.setAutobatchEnabled(false);
			}
		}
	}

	/**
	 * Restores a backup, loading all the accounts into the underlying {@code AccountManager}.
	 * 
	 * @param filename
	 *            the backup filename.
	 * @throws FileNotFoundException
	 *             if no such backup exists.
	 * @throws IllegalArgumentException
	 *             if {@code filename} is {@code null} or blank.
	 */
	public void restoreBackup(String filename) throws FileNotFoundException
	{
		if (StringUtils.isBlank(filename))
			throw new IllegalArgumentException();

		File backupFile = getBackupFile(filename);

		if (backupFile == null)
			throw new FileNotFoundException();

		try (Storage backupStorage = new SqliteStorage("jdbc:sqlite:"
				+ backupFile))
		{
			backupStorage.connect();

			List<Storage.Entry> entries = backupStorage
					.selectEntries("accounts");

			accountManager.getStorage().eraseUnit(accountManager.getUnit());

			try
			{
				accountManager.getStorage().setAutobatchEnabled(true);

				for (Storage.Entry entry : entries)
				{
					accountManager.getStorage().addEntry(
							accountManager.getUnit(), entry);
				}

				accountManager.getStorage().executeBatch();
				accountManager.getStorage().clearBatch();
			}
			finally
			{
				accountManager.getStorage().setAutobatchEnabled(false);
			}

			log(Level.INFO,
					t("restoreBackup.success.log").replace("{0}", filename));
		}
		catch (IOException ex)
		{
			log(Level.WARNING, ex);

			ExceptionHandler.handleException(ex);
		}
	}

	/**
	 * Removes a certain amount of backups starting from the oldest.
	 * 
	 * @param amount
	 *            the amount of backups to remove.
	 * @return number of backups actually removed.
	 */
	public int removeBackups(int amount)
	{
		if (amount < 0)
			throw new IllegalArgumentException();

		File[] backupFiles = getBackups();
		int effectiveAmount = 0;

		for (int i = 0; i < amount; i++)
		{
			if (i >= backupFiles.length)
				break;

			if (backupFiles[i].delete())
			{
				effectiveAmount++;
			}
		}

		log(Level.INFO,
				t("removeBackups.success").replace("{0}",
						String.valueOf(effectiveAmount)));

		return effectiveAmount;
	}

	/**
	 * Creates an array of all the backup files from the backup directory.
	 * 
	 * @return an array of {@code File} objects, each representing a backup.
	 */
	public File[] getBackups()
	{
		File backupDir = getDataFile(getConfig("config.yml").getString(
				"backup.path"));
		File[] backupFiles = backupDir.listFiles();

		if (backupFiles == null)
			return new File[0];

		Arrays.sort(backupFiles);

		return backupFiles;
	}

	public File[] getBackups(Comparator<File> comparator)
	{
		if (comparator == null)
			throw new IllegalArgumentException();

		File[] backupFiles = getBackups();

		Arrays.sort(backupFiles, comparator);

		return backupFiles;
	}

	/**
	 * Creates a backup {@code File} object with the given filename
	 * relative to the backup directory.
	 * 
	 * @param filename
	 *            the backup filename.
	 * @return the backup file, or {@code null} if a backup
	 *         with the given filename does not exist.
	 * @throws IllegalArgumentException
	 *             if {@code filename} is {@code null} or blank.
	 */
	public File getBackupFile(String filename)
	{
		if (StringUtils.isBlank(filename))
			throw new IllegalArgumentException();

		File backupDir = getDataFile(getConfig("config.yml").getString(
				"backup.path"));
		File backupFile = new File(backupDir, filename);

		if (!backupFile.exists())
			return null;

		return backupFile;
	}

	public Date parseBackupFilename(String filename) throws ParseException
	{
		if (StringUtils.isBlank(filename))
			throw new IllegalArgumentException();

		return buildDateFormat().parse(filename);
	}

	private String formatBackupFilename(Date date)
	{
		if (date == null)
			throw new IllegalArgumentException();

		return buildDateFormat().format(date);
	}

	private DateFormat buildDateFormat()
	{
		String backupFilenameFormat = getConfig("config.yml").getString(
				"backup.filename");
		DateFormat dateFormat = new SimpleDateFormat(backupFilenameFormat);

		return dateFormat;
	}

	private synchronized File allocateBackupFileForDate(Date date)
	{
		if (date == null)
			throw new IllegalArgumentException();

		File backupDir = getDataFile(getConfig("config.yml").getString(
				"backup.path"));
		File backupFile;
		int suffixIdx = 0;

		do
		{
			suffixIdx++;

			String filename = formatBackupFilename(date);

			if (suffixIdx > 1)
			{
				filename += "__" + suffixIdx;
			}

			backupFile = new File(backupDir, filename + ".db");
		}
		while (backupFile.exists());

		backupDir.getParentFile().mkdirs();
		backupDir.mkdir();

		try
		{
			backupFile.createNewFile();
		}
		catch (IOException ex)
		{
			log(Level.WARNING, ex);
		}

		return backupFile;
	}

	/**
	 * Recommended task period of {@code BackupManager} running as a Bukkit task.
	 */
	public static final long TASK_PERIOD = TimeUnit.SECONDS.convert(2,
			TimeUnit.TICKS);

	private Timer timer;
	private AccountManager accountManager;
}

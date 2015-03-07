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
package io.github.lucaseasedup.logit.logging;

import io.github.lucaseasedup.logit.LogItCore;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import org.bukkit.ChatColor;

public final class LogItCoreLogger
{
	public LogItCoreLogger(LogItCore core)
	{
		if (core == null)
			throw new IllegalArgumentException();

		this.core = core;
	}

	public void open()
	{
		openLogFile(core.getConfig("config.yml").getString(
				"logging.file.filename"));
	}

	public void close()
	{
		if (logFileWriter != null)
		{
			try
			{
				logFileWriter.close();
			}
			catch (IOException ex)
			{
				log(Level.WARNING, "Could not close log file.", ex);
			}
			finally
			{
				logFileWriter = null;
			}
		}
	}

	/**
	 * Logs a message in the name of LogIt.
	 * <p>
	 * The logger message will be saved in a log file if doing so is permitted by the appropriate configuration setting.
	 * 
	 * @param level
	 *            the message level.
	 * @param message
	 *            the message to be logged.
	 * @throws IllegalArgumentException
	 *             if {@code level} or {@code message} is {@code null}.
	 * @see #log(Level, String, Throwable)
	 */
	public synchronized void log(Level level, String message)
	{
		if (level == null || message == null)
			throw new IllegalArgumentException();

		if (core.getConfig("config.yml") != null
				&& core.getConfig("config.yml").isLoaded())
		{
			boolean fileLogEnabled = core.getConfig("config.yml").getBoolean(
					"logging.file.enabled");
			int fileLogLevel = core.getConfig("config.yml").getInt(
					"logging.file.level");

			if (fileLogEnabled && level.intValue() >= fileLogLevel)
			{
				try
				{
					getLogFileWriter()
							.write(LOG_DATE_FORMAT.format(new Date()));
					getLogFileWriter().write(" [");
					getLogFileWriter().write(level.getName());
					getLogFileWriter().write("] ");
					getLogFileWriter().write(ChatColor.stripColor(message));
					getLogFileWriter().write("\n");
					getLogFileWriter().flush();
				}
				catch (IOException ex)
				{
					core.getPlugin().getLogger()
							.log(Level.WARNING, "Could not log to file.", ex);
				}
			}

			if (core.getConfig("config.yml").getBoolean(
					"logging.verboseConsole"))
			{
				System.out.println("[" + level + "] "
						+ ChatColor.stripColor(message));

				return;
			}
		}

		core.getPlugin().getLogger().log(level, ChatColor.stripColor(message));
	}

	/**
	 * Logs a message with a {@code Throwable} in the name of LogIt.
	 * <p>
	 * The logger message will be saved in a log file if doing so is permitted by the appropriate configuration setting.
	 * 
	 * @param level
	 *            the message level.
	 * @param message
	 *            the message to be logged.
	 * @param throwable
	 *            the throwable whose stack trace should be appended to the log.
	 * @see #log(Level, String)
	 */
	public synchronized void log(Level level, String message,
			Throwable throwable)
	{
		StringWriter sw = new StringWriter();

		try (PrintWriter pw = new PrintWriter(sw))
		{
			throwable.printStackTrace(pw);
		}

		log(level, message + " [Exception stack trace:\n" + sw.toString() + "]");
	}

	/**
	 * Logs a {@code Throwable} in the name of LogIt.
	 * <p>
	 * The logger message will be saved in a log file if doing so is permitted by the appropriate configuration setting.
	 * 
	 * @param level
	 *            the logging level.
	 * @param throwable
	 *            the throwable to be logged.
	 * @see #log(Level, String, Throwable)
	 */
	public synchronized void log(Level level, Throwable throwable)
	{
		StringWriter sw = new StringWriter();

		try (PrintWriter pw = new PrintWriter(sw))
		{
			throwable.printStackTrace(pw);
		}

		log(level, "Caught exception:\n" + sw.toString());
	}

	private void openLogFile(String filename)
	{
		File logFile = core.getDataFile(filename);

		if (logFile.length() > 300000)
		{
			int suffix = 0;
			File nextLogFile;

			do
			{
				suffix++;
				nextLogFile = core.getDataFile(filename + "." + suffix);
			}
			while (nextLogFile.exists());

			logFile.renameTo(nextLogFile);
		}

		try
		{
			logFileWriter = new FileWriter(logFile, true);
		}
		catch (IOException ex)
		{
			core.getPlugin()
					.getLogger()
					.log(Level.WARNING, "Could not open log file for writing.",
							ex);
		}
	}

	private FileWriter getLogFileWriter()
	{
		if (logFileWriter == null)
			throw new IllegalStateException("Log file not opened");

		return logFileWriter;
	}

	private static final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private final LogItCore core;
	private FileWriter logFileWriter;
}

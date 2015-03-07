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
package io.github.lucaseasedup.logit.command.hub;

import static io.github.lucaseasedup.logit.message.MessageHelper.sendMsg;
import static io.github.lucaseasedup.logit.message.MessageHelper.t;
import io.github.lucaseasedup.logit.account.Account;
import io.github.lucaseasedup.logit.command.CommandAccess;
import io.github.lucaseasedup.logit.command.CommandHelpLine;
import io.github.lucaseasedup.logit.command.wizard.ConfirmationWizard;
import io.github.lucaseasedup.logit.common.ReportedException;
import io.github.lucaseasedup.logit.security.AuthMePasswordHelper;
import io.github.lucaseasedup.logit.storage.MySqlStorage;
import io.github.lucaseasedup.logit.storage.SqliteStorage;
import io.github.lucaseasedup.logit.storage.Storage;
import io.github.lucaseasedup.logit.util.IniUtils;
import it.sauronsoftware.base64.Base64;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public final class ImportAuthMeHubCommand extends HubCommand
{
	public ImportAuthMeHubCommand()
	{
		super("import authme", new String[] {}, new CommandAccess.Builder()
				.permission("logit.import").playerOnly(false)
				.runningCoreRequired(true).build(),
				new CommandHelpLine.Builder().command("logit import authme")
						.descriptionLabel("subCmdDesc.import.authme").build());
	}

	@Override
	public void execute(final CommandSender sender, String[] args)
	{
		if (!new File("plugins/AuthMe").exists())
		{
			sendMsg(sender, t("import.authme.folderNotFound"));

			return;
		}

		File authMeConfigFile = new File("plugins/AuthMe/config.yml");

		if (!authMeConfigFile.exists())
		{
			sendMsg(sender, t("import.authme.configNotFound"));

			return;
		}

		final YamlConfiguration authMeConfig = YamlConfiguration
				.loadConfiguration(authMeConfigFile);

		if (sender instanceof Player)
		{
			sendMsg(sender, "");
		}

		sendMsg(sender, t("import.authme.header"));
		sendMsg(sender, t("import.authme.typeToImport"));

		new ConfirmationWizard(sender, "import", new Runnable()
		{
			@Override
			public void run()
			{
				new BukkitRunnable()
				{
					@Override
					public void run()
					{
						try
						{
							ReportedException.incrementRequestCount();

							importAccounts(sender, authMeConfig);
						}
						catch (ReportedException ex)
						{
							if (sender instanceof Player)
							{
								sendMsg(sender, t("unexpectedError"));
							}
						}
						finally
						{
							ReportedException.decrementRequestCount();
						}

						senderLocks.remove(sender);
					}
				}.runTaskAsynchronously(getPlugin());

				senderLocks.add(sender);
			}
		}).createWizard();
	}

	private void importAccounts(CommandSender sender,
			YamlConfiguration authMeConfig)
	{
		String backend = authMeConfig.getString("DataSource.backend");
		String dataSourceMySqlHost = authMeConfig
				.getString("DataSource.mySQLHost");
		String dataSourceMySqlPort = authMeConfig
				.getString("DataSource.mySQLPort");
		String dataSourceMySqlUsername = authMeConfig
				.getString("DataSource.mySQLUsername");
		String dataSourceMySqlPassword = authMeConfig
				.getString("DataSource.mySQLPassword");
		String dataSourceMySqlDatabase = authMeConfig
				.getString("DataSource.mySQLDatabase");
		String dataSourceMySqlTablename = authMeConfig
				.getString("DataSource.mySQLTablename");
		String dataSourceMySqlColumnName = authMeConfig
				.getString("DataSource.mySQLColumnName");
		String dataSourceMySqlColumnPassword = authMeConfig
				.getString("DataSource.mySQLColumnPassword");
		String dataSourceMySqlColumnIp = authMeConfig
				.getString("DataSource.mySQLColumnIp");
		String dataSourceMySqlColumnEmail = authMeConfig
				.getString("DataSource.mySQLColumnEmail");
		String dataSourceMySqlColumnLastLocX = authMeConfig
				.getString("DataSource.mySQLlastlocX");
		String dataSourceMySqlColumnLastLocY = authMeConfig
				.getString("DataSource.mySQLlastlocY");
		String dataSourceMySqlColumnLastLocZ = authMeConfig
				.getString("DataSource.mySQLlastlocZ");
		String dataSourceMySqlColumnLastLocWorld = authMeConfig
				.getString("DataSource.mySQLlastlocWorld");
		String settingsSecurityPasswordHash = authMeConfig
				.getString("settings.security.passwordHash");

		if (backend == null)
		{
			sendMsg(sender,
					t("import.authme.configPropNotFound").replace("{0}",
							"DataSource.backend"));

			return;
		}

		if (settingsSecurityPasswordHash == null)
		{
			sendMsg(sender,
					t("import.authme.configPropNotFound").replace("{0}",
							"settings.security.passwordHash"));

			return;
		}

		if (!AuthMePasswordHelper
				.validateEncryptionMethod(settingsSecurityPasswordHash))
		{
			sendMsg(sender, t("import.authme.unsupportedEncryptionMethod")
					.replace("{0}", settingsSecurityPasswordHash));

			return;
		}

		List<Account> logItAccounts = new ArrayList<>();

		try
		{
			if (backend.equalsIgnoreCase("mysql")
					|| backend.equalsIgnoreCase("sqlite"))
			{
				Storage storage;

				if (backend.equalsIgnoreCase("mysql"))
				{
					storage = new MySqlStorage(dataSourceMySqlHost + ":"
							+ dataSourceMySqlPort, dataSourceMySqlUsername,
							dataSourceMySqlPassword, dataSourceMySqlDatabase);
				}
				else
				{
					storage = new SqliteStorage("jdbc:sqlite:plugins/AuthMe/"
							+ dataSourceMySqlDatabase + ".db");
				}

				try
				{
					storage.connect();

					List<Storage.Entry> authMeEntries = storage
							.selectEntries(dataSourceMySqlTablename);

					for (Storage.Entry authMeEntry : authMeEntries)
					{
						String authMeUsername = authMeEntry
								.get(dataSourceMySqlColumnName);

						if (StringUtils.isBlank(authMeUsername))
							continue;

						Storage.Entry.Builder logItEntryBuilder = new Storage.Entry.Builder();

						logItEntryBuilder.put(keys().username(),
								StringUtils.lowerCase(authMeUsername));
						logItEntryBuilder.put(keys().password(),
								authMeEntry.get(dataSourceMySqlColumnPassword));
						logItEntryBuilder.put(keys().hashing_algorithm(),
								"authme:" + settingsSecurityPasswordHash);
						logItEntryBuilder.put(keys().ip(),
								authMeEntry.get(dataSourceMySqlColumnIp));

						String email = authMeEntry
								.get(dataSourceMySqlColumnEmail);

						if (!email.equals("your@email.com"))
						{
							logItEntryBuilder.put(keys().email(), email);
						}

						String world = authMeEntry
								.get(dataSourceMySqlColumnLastLocWorld);

						if (dataSourceMySqlColumnLastLocX != null
								&& dataSourceMySqlColumnLastLocY != null
								&& dataSourceMySqlColumnLastLocZ != null)
						{
							String x = authMeEntry
									.get(dataSourceMySqlColumnLastLocX);
							String y = authMeEntry
									.get(dataSourceMySqlColumnLastLocY);
							String z = authMeEntry
									.get(dataSourceMySqlColumnLastLocZ);

							if (!y.equals("0.0"))
							{
								Map<String, Map<String, String>> persistenceIni = new HashMap<>();
								Map<String, String> persistence = new LinkedHashMap<>();

								persistence.put("world", world);
								persistence.put("x", x);
								persistence.put("y", y);
								persistence.put("z", z);
								persistence.put("yaw", "0.0");
								persistence.put("pitch", "0.0");

								persistenceIni.put("persistence", persistence);

								String persistenceString = IniUtils
										.serialize(persistenceIni);

								if (getConfig("secret.yml").getBoolean(
										"debug.encodePersistence"))
								{
									persistenceString = Base64
											.encode(persistenceString);
								}

								logItEntryBuilder.put(keys().persistence(),
										persistenceString);
							}
						}

						logItAccounts
								.add(new Account(logItEntryBuilder.build()));
					}
				}
				finally
				{
					storage.close();
				}
			}
			else
			{
				File backendFile = new File("plugins/AuthMe/auths.db");

				if (!backendFile.exists())
				{
					sendMsg(sender,
							t("import.authme.fileNotFound").replace("{0}",
									"auths.db"));

					return;
				}

				try (BufferedReader br = new BufferedReader(new FileReader(
						backendFile)))
				{
					String line;

					while ((line = br.readLine()) != null)
					{
						String[] split = line.split(":");
						Storage.Entry.Builder logItEntryBuilder = new Storage.Entry.Builder();

						if (split.length == 0)
							continue;

						if (split.length >= 1)
						{
							if (StringUtils.isBlank(split[0]))
								continue;

							logItEntryBuilder.put(keys().username(),
									split[0].toLowerCase());
						}

						if (split.length >= 2)
						{
							logItEntryBuilder.put(keys().password(), split[1]);
							logItEntryBuilder.put(keys().hashing_algorithm(),
									"authme:" + settingsSecurityPasswordHash);
						}

						if (split.length >= 3)
						{
							logItEntryBuilder.put(keys().ip(), split[2]);
						}

						if (split.length >= 4)
						{
							logItEntryBuilder
									.put(keys().last_active_date(),
											String.valueOf(Long
													.parseLong(split[3]) / 1000));
						}

						if (split.length >= 8 && !split[5].equals("0.0"))
						{
							Map<String, Map<String, String>> persistenceIni = new HashMap<>();
							Map<String, String> persistence = new LinkedHashMap<>();

							persistence.put("world", split[7]);
							persistence.put("x", split[4]);
							persistence.put("y", split[5]);
							persistence.put("z", split[6]);
							persistence.put("yaw", "0.0");
							persistence.put("pitch", "0.0");

							persistenceIni.put("persistence", persistence);

							logItEntryBuilder
									.put(keys().persistence(), Base64
											.encode(IniUtils
													.serialize(persistenceIni)));
						}

						if (split.length >= 9)
						{
							String email = split[8].toLowerCase();

							if (!email.equals("your@email.com"))
							{
								logItEntryBuilder.put(keys().email(), email);
							}
						}

						logItAccounts
								.add(new Account(logItEntryBuilder.build()));
					}
				}
			}

			Set<String> registeredUsernames = getAccountManager()
					.getRegisteredUsernames();
			int accountsImported = 0;

			Iterator<Account> it = logItAccounts.iterator();

			while (it.hasNext())
			{
				Account logItAccount = it.next();

				if (registeredUsernames != null
						&& registeredUsernames.contains(logItAccount
								.getUsername()))
				{
					it.remove();

					continue;
				}

				accountsImported++;
			}

			getAccountManager().insertAccounts(
					logItAccounts.toArray(new Account[0]));

			log(Level.INFO,
					t("import.authme.success").replace("{0}",
							String.valueOf(accountsImported)));

			if (sender instanceof Player)
			{
				sendMsg(sender,
						t("import.authme.success").replace("{0}",
								String.valueOf(accountsImported)));
			}
		}
		catch (IOException ex)
		{
			log(Level.WARNING, ex);

			ReportedException.throwNew(ex);
		}
	}

	private final Set<CommandSender> senderLocks = new HashSet<>();
}

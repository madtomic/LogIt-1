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
import io.github.lucaseasedup.logit.storage.SelectorConstant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import io.github.lucaseasedup.logit.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class StatsHubCommand extends HubCommand
{
	public StatsHubCommand()
	{
		super("stats", new String[] {}, new CommandAccess.Builder()
				.permission("logit.stats").playerOnly(false)
				.runningCoreRequired(true).build(),
				new CommandHelpLine.Builder().command("logit stats")
						.descriptionLabel("subCmdDesc.stats").build());
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		List<Account> accounts = getAccountManager().selectAccounts(
				Arrays.asList(keys().username(), keys().ip()),
				new SelectorConstant(true));

		Set<String> uniqueIps = null;

		if (accounts != null)
		{
			uniqueIps = new HashSet<>();

			for (Account account : accounts)
			{
				String ip = account.getIp();

				if (!StringUtils.isBlank(ip))
				{
					uniqueIps.add(ip);
				}
			}
		}

		int backupCount = getBackupManager().getBackups().length;

		if (sender instanceof Player)
		{
			sendMsg(sender, "");
		}

		sendMsg(sender, t("stats.header"));
		sendMsg(sender,
				t("stats.accountCount").replace(
						"{0}",
						(accounts != null) ? String.valueOf(accounts.size())
								: "?"));
		sendMsg(sender,
				t("stats.uniqueIps").replace(
						"{0}",
						(uniqueIps != null) ? String.valueOf(uniqueIps.size())
								: "?"));
		sendMsg(sender,
				t("stats.backupCount").replace("{0}",
						String.valueOf(backupCount)));

		if (getConfig("config.yml").getBoolean("stats.enabled"))
		{
			int logins = getConfig("stats.yml").getInt("logins");
			int passwordChanges = getConfig("stats.yml").getInt(
					"passwordChanges");

			sendMsg(sender, "");
			sendMsg(sender,
					t("stats.logins").replace("{0}", String.valueOf(logins)));
			sendMsg(sender,
					t("stats.passwordChanges").replace("{0}",
							String.valueOf(passwordChanges)));
		}

		if (sender instanceof Player)
		{
			sendMsg(sender, "");
		}
	}
}

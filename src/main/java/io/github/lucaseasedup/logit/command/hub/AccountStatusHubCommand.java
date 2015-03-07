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
import io.github.lucaseasedup.logit.command.CommandAccess;
import io.github.lucaseasedup.logit.command.CommandHelpLine;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AccountStatusHubCommand extends HubCommand
{
	public AccountStatusHubCommand()
	{
		super("account status", new String[] { "username" },
				new CommandAccess.Builder().permission("logit.account.status")
						.playerOnly(false).runningCoreRequired(true).build(),
				new CommandHelpLine.Builder().command("logit account status")
						.descriptionLabel("subCmdDesc.account.status").build());
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		if (sender instanceof Player)
		{
			sendMsg(sender, "");
		}

		sendMsg(sender, t("accountStatus.header"));
		sendMsg(sender,
				t("accountStatus.username").replace("{0}",
						args[0].toLowerCase()));

		String status;

		if (getAccountManager().isRegistered(args[0]))
		{
			status = t("accountStatus.status.registered");
		}
		else
		{
			status = t("accountStatus.status.notRegistered");
		}

		sendMsg(sender, t("accountStatus.status").replace("{0}", status));

		if (sender instanceof Player)
		{
			sendMsg(sender, "");
		}
	}

	@Override
	public List<String> complete(CommandSender sender, String[] args)
	{
		if (!getConfig("secret.yml").getBoolean("tabCompletion"))
			return null;

		if (args.length == 1)
		{
			return getTabCompleter().completeUsername(args[0]);
		}

		return null;
	}
}

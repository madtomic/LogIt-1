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
import io.github.lucaseasedup.logit.config.PredefinedConfiguration;
import io.github.lucaseasedup.logit.config.Property;
import org.bukkit.command.CommandSender;

public final class ConfigGetHubCommand extends HubCommand
{
	public ConfigGetHubCommand()
	{
		super("config get", new String[] { "path" },
				new CommandAccess.Builder().permission("logit.config.get")
						.playerOnly(false).runningCoreRequired(true).build(),
				new CommandHelpLine.Builder().command("logit config get")
						.descriptionLabel("subCmdDesc.config.get").build());
	}

	@Override
	public void execute(CommandSender sender, String[] args)
	{
		String hyphenatedPath = args[0];
		String camelCasePath = PredefinedConfiguration
				.getCamelCasePath(hyphenatedPath);
		Property property;

		if (!getConfig("config.yml").contains(hyphenatedPath))
		{
			if (!getConfig("config.yml").contains(camelCasePath))
			{
				sendMsg(sender,
						t("config.propertyNotFound").replace("{0}",
								hyphenatedPath));

				return;
			}
			else
			{
				property = getConfig("config.yml").getProperty(camelCasePath);
			}
		}
		else
		{
			property = getConfig("config.yml").getProperty(hyphenatedPath);
		}

		sendMsg(sender,
				t("config.get.property").replace("{0}", property.getPath())
						.replace("{1}", property.getStringifiedValue()));
	}
}

/*
 * ProfileViewWizard.java
 *
 * Copyright (C) 2012-2014 LucasEasedUp
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
package io.github.lucaseasedup.logit.command.wizard;

import static io.github.lucaseasedup.logit.message.MessageHelper.t;
import io.github.lucaseasedup.logit.profile.field.Field;
import java.util.List;
import org.bukkit.command.CommandSender;

public final class ProfileViewWizard extends Wizard
{
	public ProfileViewWizard(CommandSender sender, String playerName)
	{
		super(sender, Step.VIEW);

		this.playerName = playerName;
	}

	@Override
	protected void onCreate()
	{
		List<Field> fields = getProfileManager().getDefinedFields();

		sendMessage("");
		sendMessage(t("profile.view.header").replace("{0}", playerName));
		sendMessage(t("wizard.orangeHorizontalLine"));

		if (!fields.isEmpty())
		{
			for (Field field : fields)
			{
				Object value = getProfileManager().getProfileObject(playerName,
						field.getName());

				if (value == null)
				{
					value = "";
				}

				sendMessage(t("profile.view.field").replace("{0}",
						field.getName()).replace("{1}", value.toString()));
			}
		}
		else
		{
			sendMessage(t("profile.view.noFields"));
		}

		sendMessage(t("wizard.orangeHorizontalLine"));
		cancelWizard();
	}

	@Override
	protected void onMessage(String message)
	{
		// ProfileViewWizard is cancelled as soon as it is created.
	}

	public static enum Step
	{
		VIEW
	}

	private final String playerName;
}

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
package io.github.lucaseasedup.logit.message;

import static io.github.lucaseasedup.logit.message.MessageHelper.t;
import io.github.lucaseasedup.logit.bukkit.BukkitLogItCore;

import org.bukkit.entity.Player;

public final class JoinMessageGenerator
{
	private JoinMessageGenerator()
	{
	}

	public static String generate(Player player, boolean revealSpawnWorld)
	{
		BukkitLogItCore core = BukkitLogItCore.getInstance();

		assert core != null;

		String message;

		if (core.getConfig("config.yml").getBoolean("messages.beautify"))
		{
			message = t("join.beautified");
		}
		else
		{
			message = t("join.native");
		}

		String inWorld;

		if (core.getConfig("config.yml").getBoolean("messages.beautify"))
		{
			inWorld = t("join.beautified.inWorld");
		}
		else
		{
			inWorld = t("join.native.inWorld");
		}

		if (revealSpawnWorld)
		{
			message = message.replace("{1}",
					inWorld.replace("{0}", player.getWorld().getName()));
		}
		else
		{
			message = message.replace("{1}", "");
		}

		return message.replace("{0}", player.getName());
	}

	/*
	 * public static String getWorldAlias(World world)
	 * {
	 * LogItCore core = LogItCore.getInstance();
	 * assert core != null;
	 * if (!core.getConfig("config.yml").getBoolean("messages.multiverseHook")
	 * || !Bukkit.getPluginManager().isPluginEnabled("Multiverse-Core"))
	 * {
	 * return world.getName();
	 * }
	 * Plugin plugin = Bukkit.getPluginManager().getPlugin("Multiverse-Core");
	 * if (!(plugin instanceof MultiverseCore))
	 * return world.getName();
	 * return ((MultiverseCore) plugin).getMVWorldManager().getMVWorld(world).getAlias();
	 * }
	 */
}

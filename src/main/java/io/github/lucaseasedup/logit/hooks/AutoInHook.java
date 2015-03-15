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
package io.github.lucaseasedup.logit.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class AutoInHook
{
	private AutoInHook()
	{
	}

	/**
	 * Uses AutoIn to check if a player is premium.
	 * 
	 * @param player
	 *            the player.
	 * @return {@code true} if the player is premium; {@code false} if the player is non-premium
	 *         or AutoIn hasn't been found.
	 */
	public static boolean isPremium(Player player)
	{
		if (player == null)
			throw new IllegalArgumentException();

		if (!Bukkit.getPluginManager().isPluginEnabled("AutoIn"))
			return false;

		return com.gmail.bartlomiejkmazur.autoin.api.APICore.getAPI().getPremiumStatus(player.getName()).isPremium();
	}

	public static void registerPlugin()
	{
		if (Bukkit.getPluginManager().getPlugin("AutoIn") == null)
			return;
		com.gmail.bartlomiejkmazur.autoin.api.APICore.getAPI().addLoginPlugin("LogIt", new AutoInPlugin());
	}
}

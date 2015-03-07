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

import io.github.lucaseasedup.logit.LogItCore;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

public final class BungeeHook
{
	private BungeeHook()
	{
	}

	public static void sendPlayerToServer(Player player, String server)
	{
		if (player == null || server == null || server.isEmpty())
			throw new IllegalArgumentException();
		
		  ByteArrayDataOutput out = ByteStreams.newDataOutput();
		  out.writeUTF("Connect");
		  out.writeUTF(server);
		  
		  player.sendPluginMessage(LogItCore.getInstance().getPlugin(), "BungeeCord", out.toByteArray());
		  out = null;
	}

	@SuppressWarnings("deprecation")
	public static void sendPlayerToServer(String player, String server)
	{
		sendPlayerToServer(Bukkit.getPlayerExact(player), server);
	}
}

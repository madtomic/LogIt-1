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

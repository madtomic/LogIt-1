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
package io.github.lucaseasedup.logit.channels;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import io.github.lucaseasedup.logit.bungee.BungeeLogItCoreObject;
import io.github.lucaseasedup.logit.channels.listeners.server.NewClientPacketListener;
import io.github.lucaseasedup.logit.channels.packets.NewClientPacket;
import io.github.lucaseasedup.logit.util.ExceptionHandler;

public class ChannelServer extends BungeeLogItCoreObject implements IChannelManager
{
	public ChannelServer()
	{
		servers = new ArrayList<ConnectedServer>(5);
		codec = new ChannelCodec();
		registerPackets();
		getPlugin().getProxy().registerChannel("LogIt");
		listener = new CustomChannelListener();
		getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), listener);
		isRunning = true;
		logInfo("Channel opened, waiting for servers...");
	}
	
	public void stop()
	{
		getPlugin().getProxy().unregisterChannel("LogIt");
		getPlugin().getProxy().getPluginManager().unregisterListener(listener);
		logInfo("Channel closed");
		listener = null;
		isRunning = false;
	}
	
	private void registerPackets()
	{
		codec.registerPacket(new NewClientPacket(), new NewClientPacketListener());
	}
	
	public boolean canSendPackets()
	{
		return true;
	}

	public void sendPacket(byte[] packet, UUID serverUuid)
	{
		for(ConnectedServer server : getConnectedServers())
		{
			if(server.getUuid().equals(serverUuid))
			{
				getPlugin().getProxy().getServerInfo(server.getBungeeInstanceName()).sendData("LogIt", packet);
				return;
			}
		}
	}
	
	public void sendPacket(byte[] packet, String serverName)
	{
		for(ConnectedServer server : getConnectedServers())
		{
			if(server.getBungeeInstanceName().equals(serverName))
			{
				getPlugin().getProxy().getServerInfo(serverName).sendData("LogIt", packet);
				return;
			}
		}
	}

	public boolean isRunning()
	{
		return isRunning;
	}
	
	public List<ConnectedServer> getConnectedServers()
	{
		return servers;
	}
	
	public ChannelCodec getChannelCodec()
	{
		return codec;
	}
	
	public final class CustomChannelListener implements Listener
	{
		@EventHandler
		public void onMessageReceive(PluginMessageEvent e)
		{
			if(!e.getTag().equals("LogIt"))
			{
				return;
			}
			UserConnection sender = (UserConnection)e.getReceiver();
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(e.getData()));
			try
			{
				List<String> messages = new ArrayList<String>();
				while(data.available() >= 1)
				{
					messages.add(data.readUTF());
				}
				codec.handlePacket(messages, sender.getServer().getInfo().getName());
			}
			catch (IOException e1)
			{
				ExceptionHandler.handleException(e1);
			}
		}
	}
	
	private List<ConnectedServer> servers;
	
	private boolean isRunning = false;
	private CustomChannelListener listener;
	private ChannelCodec codec;
}

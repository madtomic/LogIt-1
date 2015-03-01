package io.github.lucaseasedup.logit.channels;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.channels.listeners.client.NewClientResponseListener;
import io.github.lucaseasedup.logit.channels.packets.DisconnectPacket;
import io.github.lucaseasedup.logit.channels.packets.NewClientPacket;
import io.github.lucaseasedup.logit.channels.packets.NewClientResponsePacket;
import io.github.lucaseasedup.logit.channels.packets.PingPacket;

public class ChannelClient extends LogItCoreObject implements IChannelManager
{
	public ChannelClient()
	{
		codec = new ChannelCodec();
		registerPackets();
		listener = new CustomMessageListener();
		getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(getPlugin(), "LogIt");
		getPlugin().getServer().getMessenger().registerIncomingPluginChannel(getPlugin(), "LogIt", listener);
		keeper = new ConnectionKeeper();
		getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(), keeper, 15*20, 15*20);
		getPlugin().getServer().getPluginManager().registerEvents(keeper, getPlugin());
		isRunning = true;
	}
	
	public void stop()
	{
		getPlugin().getServer().getMessenger().unregisterIncomingPluginChannel(getPlugin(), "LogIt", listener);
		getPlugin().getServer().getMessenger().unregisterOutgoingPluginChannel(getPlugin(), "LogIt");
		PlayerQuitEvent.getHandlerList().unregister(keeper);
		listener = null;
		keeper = null;
		isRunning = false;
	}
	
	private void registerPackets()
	{
		codec.registerPacket(new NewClientResponsePacket(), new NewClientResponseListener());
	}
	
	public boolean isRunning()
	{
		return isRunning;
	}
	
	public boolean isConnected()
	{
		return isConnected;
	}
	
	public void connect()
	{
		if(isConnected())
			throw new IllegalStateException("Arleady connected!");
		sendPacket(codec.encodePacket(new NewClientPacket(getPlugin().getDescription().getVersion())));
	}
	
	public void disconnect()
	{
		if(!isConnected())
			throw new IllegalStateException("Arleady disconnected!");
		
		sendPacket(codec.encodePacket(new NewClientPacket(getPlugin().getDescription().getVersion())));
		isConnected = false;
	}
	
	public void setConnected(boolean connected)
	{
		this.isConnected = connected;
	}
	
	public void setUuid(UUID uuid)
	{
		this.thisInstanceUuid = uuid;
	}
	
	public UUID getUuid()
	{
		return thisInstanceUuid;
	}
	
	public boolean canSendPackets()
	{
		return Bukkit.getOnlinePlayers().length >= 1;
	}
	
	public void sendPacket(byte[] data)
	{
		if(!canSendPackets())
			return;
		Bukkit.getOnlinePlayers()[0].sendPluginMessage(getPlugin(), "LogIt", data);
	}
	
	private class ConnectionKeeper implements Runnable, Listener
	{
		@Override
		public void run()
		{
			if(!isRunning)
			{
				return;
			}
			
			if(!canSendPackets())
			{
				isConnected = false;
				return;
			}
			
			if(isConnected())
			{
				sendPacket(codec.encodePacket(new PingPacket(thisInstanceUuid)));
				return;
			}
			
			connect();
		}
		
		@EventHandler(priority = EventPriority.LOWEST)
		public void onLastPlayerLeave(PlayerQuitEvent e)
		{
			if(!isConnected())
			{
				return;
			}
			
			if(Bukkit.getOnlinePlayers().length <= 1)
			{
				disconnect();
			}
		}
		
		@EventHandler(priority = EventPriority.HIGHEST)
		public void onFirstPlayerJoin(PlayerJoinEvent e)
		{
			if(isConnected())
			{
				return;
			}
			
			if((Bukkit.getOnlinePlayers().length <= 1) && !isConnected())
			{
				connect();
			}
		}
	}
	
	private class CustomMessageListener implements PluginMessageListener
	{
		@Override
		public void onPluginMessageReceived(String arg0, Player player, byte[] bytes)
		{
			System.out.println(arg0);
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(bytes));
			try
			{
				List<String> messages = new ArrayList<String>();
				while(data.available() >= 1)
				{
					messages.add(data.readUTF());
				}
				codec.handlePacket(messages);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	private boolean isRunning = false;
	private boolean isConnected = false;
	private UUID thisInstanceUuid;
	private CustomMessageListener listener;
	private ChannelCodec codec;
	private ConnectionKeeper keeper;
}

package io.github.lucaseasedup.logit.channels;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.channels.packets.NewClientPacket;

public class ChannelClient extends LogItCoreObject
{
	@SuppressWarnings("deprecation")
	public ChannelClient()
	{
		codec = new ChannelCodec();
		registerPackets();
		listener = new CustomMessageListener();
		getPlugin().getServer().getMessenger().registerOutgoingPluginChannel(getPlugin(), "LogIt");
		getPlugin().getServer().getMessenger().registerIncomingPluginChannel(getPlugin(), "LogIt", listener);
		keeper = new ConnectionKeeper();
		getPlugin().getServer().getScheduler().scheduleAsyncRepeatingTask(getPlugin(), keeper, 15*20, 15*20);
		//System.out.println("debug");
		//getPlugin().getServer().sendPluginMessage(getPlugin(), "LogIt", codec.encodePacket(new NewClientPacket("test")));
	}
	
	public void stop()
	{
		getPlugin().getServer().getMessenger().unregisterIncomingPluginChannel(getPlugin(), "LogIt", listener);
		getPlugin().getServer().getMessenger().unregisterOutgoingPluginChannel(getPlugin(), "LogIt");
		listener = null;
	}
	
	private void registerPackets()
	{
		
	}
	
	public boolean isConnected()
	{
		return isConnected;
	}
	
	public boolean canSendPackets()
	{
		return Bukkit.getOnlinePlayers().length >= 1;
	}
	
	public void sendPacket(byte[] data)
	{
		Bukkit.getOnlinePlayers()[0].sendPluginMessage(getPlugin(), "LogIt", data);
	}
	
	private class ConnectionKeeper implements Runnable
	{
		@Override
		public void run()
		{
			System.out.println("debug");
			if(isConnected())
			{
				return;
			}
			
			if(!canSendPackets())
			{
				return;
			}
			
			sendPacket(codec.encodePacket(new NewClientPacket("test")));
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
				System.out.println(messages.size()); // DEBUG
				codec.handlePacket(messages);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
	}
	
	private boolean isConnected = false;
	private CustomMessageListener listener;
	private ChannelCodec codec;
	private ConnectionKeeper keeper;
}

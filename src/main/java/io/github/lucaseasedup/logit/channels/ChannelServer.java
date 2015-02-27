package io.github.lucaseasedup.logit.channels;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import io.github.lucaseasedup.logit.bungee.BungeeLogItCoreObject;

public class ChannelServer extends BungeeLogItCoreObject
{
	public ChannelServer()
	{
		codec = new ChannelCodec();
		registerPackets();
		getPlugin().getProxy().registerChannel("LogIt");
		listener = new CustomChannelListener();
		getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), listener);
		logInfo("Channel opened, waiting for servers...");
	}
	
	public void stop()
	{
		getPlugin().getProxy().unregisterChannel("LogIt");
		getPlugin().getProxy().getPluginManager().unregisterListener(listener);
		logInfo("Channel closed");
	}
	
	private void registerPackets()
	{
		
	}
	
	private final class CustomChannelListener implements Listener
	{
		@EventHandler
		public void onMessageReceive(PluginMessageEvent e)
		{
			if(!e.getTag().equals("LogIt"))
			{
				return;
			}
			
			DataInputStream data = new DataInputStream(new ByteArrayInputStream(e.getData()));
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
	
	private CustomChannelListener listener;
	private ChannelCodec codec;
}

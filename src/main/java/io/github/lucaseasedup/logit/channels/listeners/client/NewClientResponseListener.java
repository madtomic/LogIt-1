package io.github.lucaseasedup.logit.channels.listeners.client;

import java.util.logging.Level;

import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.channels.IPacketListener;
import io.github.lucaseasedup.logit.channels.packets.IPacket;
import io.github.lucaseasedup.logit.channels.packets.NewClientResponsePacket;

public class NewClientResponseListener extends LogItCoreObject implements IPacketListener
{
	@Override
	public void onPacketReceiving(IPacket packet, String server)
	{
		NewClientResponsePacket p = (NewClientResponsePacket)packet;
		
		getCore().getChannelManager().setConnected(true);
		getCore().getChannelManager().setUuid(p.getUuid());
		getCore().log(Level.INFO, "Connected to BungeeCord! Given UUID: "+p.getUuid().toString());
	}
}

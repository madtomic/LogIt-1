package io.github.lucaseasedup.logit.channels.listeners.server;

import java.util.UUID;

import io.github.lucaseasedup.logit.bungee.BungeeLogItCoreObject;
import io.github.lucaseasedup.logit.channels.ConnectedServer;
import io.github.lucaseasedup.logit.channels.IPacketListener;
import io.github.lucaseasedup.logit.channels.packets.IPacket;
import io.github.lucaseasedup.logit.channels.packets.NewClientResponsePacket;

public class NewClientPacketListener extends BungeeLogItCoreObject implements IPacketListener
{
	@Override
	public void onPacketReceiving(IPacket packet, String server)
	{
		ConnectedServer connserver = new ConnectedServer(UUID.randomUUID(), server);
		
		getCore().getChannelManager().getConnectedServers().add(connserver);
		getCore().getChannelManager().sendPacket(getCore().getChannelManager().getChannelCodec().encodePacket(new NewClientResponsePacket(connserver.getUuid())), server);
		getCore().logInfo("New LogIt instance connected from server "+server+". Given UUID: "+connserver.getUuid());
	}
}

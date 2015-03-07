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

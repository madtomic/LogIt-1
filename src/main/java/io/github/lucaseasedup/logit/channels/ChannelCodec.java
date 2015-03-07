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

import io.github.lucaseasedup.logit.channels.packets.IPacket;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChannelCodec
{
	private List<PacketInfo> registeredPackets;
	
	public ChannelCodec()
	{
		registeredPackets = new ArrayList<PacketInfo>(20);
	}
	
	public void registerPacket(IPacket packetInstance)
	{
		registeredPackets.add(new PacketInfo(packetInstance, null));
	}
	
	public void registerPacket(IPacket packetInstance, IPacketListener packetListener)
	{
		registeredPackets.add(new PacketInfo(packetInstance, packetListener));
	}
	
	public void handlePacket(List<String> messages)
	{
		handlePacket(messages, null);
	}
	
	public void handlePacket(List<String> messages, String server)
	{
		if(messages == null || messages.size() < 1)
		{
			throw new IllegalArgumentException();
		}
		
		String packetName = messages.get(0);
		for(PacketInfo packet : registeredPackets)
		{
			if(packet.getInstance().getClass().getSimpleName().equals(packetName))
			{
				if(packet.hasListener())
				{
					messages.remove(0);
					packet.getPacketListener().onPacketReceiving(packet.getInstance().decode(messages), server);
				}
				return;
			}
		}
	}
	
	public byte[] encodePacket(IPacket packet)
	{
		List<String> message = packet.encode();
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream datastream = new DataOutputStream(bytes);
		try
		{
			for(String entry : message)
			{
				datastream.writeUTF(entry);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return bytes.toByteArray();
	}
	
	private class PacketInfo
	{
		private @Nonnull IPacket packetInstance;
		private @Nullable IPacketListener listener;
		
		public PacketInfo(@Nonnull IPacket packetInstance, @Nullable IPacketListener listener)
		{
			this.packetInstance = packetInstance;
			this.listener = listener;
		}
		
		public IPacket getInstance()
		{
			return packetInstance;
		}
		
		public boolean hasListener()
		{
			return listener != null;
		}
		
		public IPacketListener getPacketListener()
		{
			return listener;
		}
	}
}

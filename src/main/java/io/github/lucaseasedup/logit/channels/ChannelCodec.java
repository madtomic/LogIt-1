package io.github.lucaseasedup.logit.channels;

import io.github.lucaseasedup.logit.channels.packets.IPacket;

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
		if(messages == null || messages.size() < 1)
		{
			throw new IllegalArgumentException();
		}
		
		String packetName = messages.get(0);
		System.out.println("Otrzymano pakiet "+packetName); // DEBUG
		for(PacketInfo packet : registeredPackets)
		{
			if(packet.getInstance().getClass().getName().equals(packetName))
			{
				if(packet.hasListener())
				{
					System.out.println("Listener pakietu "+packetName); // DEBUG
					messages.remove(0);
					packet.getPacketListener().onPacketReceiving(packet.getInstance().decode(messages));
				}
				return;
			}
		}
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

package io.github.lucaseasedup.logit.channels;

import io.github.lucaseasedup.logit.channels.packets.IPacket;

public interface IPacketListener
{
	public void onPacketReceiving(IPacket packet);
}

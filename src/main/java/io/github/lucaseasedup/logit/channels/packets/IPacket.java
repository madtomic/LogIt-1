package io.github.lucaseasedup.logit.channels.packets;

import java.util.List;

public interface IPacket
{
	public IPacket decode(List<String> message);
	public List<String> encode();
}

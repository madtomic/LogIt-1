package io.github.lucaseasedup.logit.channels.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PingPacket implements IPacket
{
	public PingPacket()
	{
	}
	
	public PingPacket(UUID uuid)
	{
		this.uuid = uuid;
	}
	
	@Override
	public IPacket decode(List<String> message)
	{
		return new PingPacket(UUID.fromString(message.get(0)));
	}

	@Override
	public List<String> encode()
	{
		List<String> temp = new ArrayList<String>(1);
		temp.add(uuid.toString());
		return temp;
	}
	
	private UUID uuid;
}

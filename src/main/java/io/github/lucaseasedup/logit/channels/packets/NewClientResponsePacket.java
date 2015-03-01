package io.github.lucaseasedup.logit.channels.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NewClientResponsePacket implements IPacket
{
	public NewClientResponsePacket()
	{
	}
	
	public NewClientResponsePacket(UUID newUuid)
	{
		this.newUuid = newUuid;
	}
	
	public UUID getUuid()
	{
		return newUuid;
	}
	
	@Override
	public IPacket decode(List<String> message)
	{
		return new NewClientResponsePacket(UUID.fromString(message.get(0)));
	}

	@Override
	public List<String> encode()
	{
		List<String> temp = new ArrayList<String>(2);
		temp.add("NewClientResponsePacket");
		temp.add(newUuid.toString());
		return temp;
	}
	
	private UUID newUuid;
}

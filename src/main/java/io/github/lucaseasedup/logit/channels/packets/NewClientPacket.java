package io.github.lucaseasedup.logit.channels.packets;

import java.util.ArrayList;
import java.util.List;

public class NewClientPacket implements IPacket
{
	private String logItVersion;
	
	public NewClientPacket(String logItVersion)
	{
		this.logItVersion = logItVersion;
	}
	
	@Override
	public IPacket decode(List<String> message)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> encode()
	{
		List<String> temp = new ArrayList<String>(1);
		temp.add(logItVersion);
		return temp;
	}
}

package io.github.lucaseasedup.logit.channels.packets;

import java.util.ArrayList;
import java.util.List;

public class NewClientPacket implements IPacket
{
	private String logItVersion;
	
	public NewClientPacket()
	{
		this.logItVersion = "Unknown";
	}
	
	public NewClientPacket(String logItVersion)
	{
		this.logItVersion = logItVersion;
	}
	
	public String getVersion()
	{
		return this.logItVersion;
	}
	
	public void setVersion(String logItVersion)
	{
		this.logItVersion = logItVersion;
	}
	
	@Override
	public IPacket decode(List<String> message)
	{
		return new NewClientPacket(message.get(0));
	}

	@Override
	public List<String> encode()
	{
		List<String> temp = new ArrayList<String>(2);
		temp.add("NewClientPacket");
		temp.add(logItVersion);
		return temp;
	}
}

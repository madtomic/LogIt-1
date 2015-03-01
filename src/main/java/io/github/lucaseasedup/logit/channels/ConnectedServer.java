package io.github.lucaseasedup.logit.channels;

import java.util.UUID;

public class ConnectedServer
{
	public ConnectedServer(UUID uuid, String bungeeInstanceName)
	{
		this.uuid = uuid;
		this.bungeeInstanceName = bungeeInstanceName;
	}
	
	public UUID getUuid()
	{
		return uuid;
	}
	
	public String getBungeeInstanceName()
	{
		return bungeeInstanceName;
	}
	
	private String bungeeInstanceName;
	private UUID uuid;
}

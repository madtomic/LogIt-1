package io.github.lucaseasedup.logit.bungee;

import java.util.logging.Logger;

public abstract class BungeeLogItCoreObject
{
	public BungeeLogItCoreObject()
	{
		core = BungeeLogItCore.getInstance();

		if (core == null)
		{
			throw new IllegalStateException("No BungeeLogItCore instance found.");
		}
	}
	
	@SuppressWarnings("static-access")
	protected final BungeeLogItCore getCore()
	{
		return core.getInstance();
	}
	
	protected final BungeeLogItPlugin getPlugin()
	{
		return core.getPlugin();
	}
	
	protected final Logger getLogger()
	{
		return core.getLogger();
	}
	
	protected final void logInfo(String message)
	{
		core.logInfo(message);
	}
	
	private final BungeeLogItCore core;
}

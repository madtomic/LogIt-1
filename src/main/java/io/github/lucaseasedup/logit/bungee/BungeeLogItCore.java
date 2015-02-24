package io.github.lucaseasedup.logit.bungee;

public final class BungeeLogItCore
{
	private BungeeLogItCore(BungeeLogItPlugin plugin)
	{
		assert plugin != null;

		this.plugin = plugin;
	}
	
	public static BungeeLogItCore getInstance()
	{
		if (instance == null)
		{
			instance = new BungeeLogItCore(BungeeLogItPlugin.getInstance());
		}

		return instance;
	}
	
	private static volatile BungeeLogItCore instance = null;

	private final BungeeLogItPlugin plugin;
	private boolean firstRun;
	private boolean started = false;
}

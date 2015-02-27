package io.github.lucaseasedup.logit.bungee;

import io.github.lucaseasedup.logit.CancelledState;
import io.github.lucaseasedup.logit.channels.ChannelServer;
import io.github.lucaseasedup.logit.common.FatalReportedException;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class BungeeLogItCore
{
	private BungeeLogItCore(BungeeLogItPlugin plugin)
	{
		assert plugin != null;

		this.plugin = plugin;
	}

	public CancelledState start() throws FatalReportedException
	{
		if (isStarted())
			throw new IllegalStateException(
					"The LogIt core has already been started.");

		firstRun = !getDataFolder().exists();
		getDataFolder().mkdir();
		
		channelManager = new ChannelServer();
		
		started = true;

		return CancelledState.NOT_CANCELLED;
	}

	public void stop()
	{
		if (!isStarted())
			throw new IllegalStateException("The LogIt core is not started.");
		
		channelManager.stop();
	}

	public boolean isStarted()
	{
		return started;
	}

	public File getDataFolder()
	{
		return getPlugin().getDataFolder();
	}

	public File getDataFile(String path)
	{
		return new File(getDataFolder(), path);
	}
	
	public Logger getLogger()
	{
		return getPlugin().getProxy().getLogger();
	}

	public boolean isFirstRun()
	{
		return firstRun;
	}
	
	public static BungeeLogItCore getInstance()
	{
		if (instance == null)
		{
			instance = new BungeeLogItCore(BungeeLogItPlugin.getInstance());
		}

		return instance;
	}

	public BungeeLogItPlugin getPlugin()
	{
		return plugin;
	}
	
	public void logInfo(String message)
	{
		getLogger().log(Level.INFO, "[LogIt] "+message);
	}
	
	private static volatile BungeeLogItCore instance = null;

	private ChannelServer channelManager;
	private final BungeeLogItPlugin plugin;
	private boolean firstRun;
	private boolean started = false;
}

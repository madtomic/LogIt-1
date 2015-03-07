/*
 * Copyright (C) 2012-2015 LucasEasedUp & NorthPL
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
	
	public ChannelServer getChannelManager()
	{
		return channelManager;
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

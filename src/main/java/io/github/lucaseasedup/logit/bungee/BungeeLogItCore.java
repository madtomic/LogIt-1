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

import io.github.lucaseasedup.logit.Core;
import io.github.lucaseasedup.logit.ICore;
import io.github.lucaseasedup.logit.channels.ChannelServer;
import io.github.lucaseasedup.logit.common.CancelledState;
import io.github.lucaseasedup.logit.common.FatalReportedException;
import io.github.lucaseasedup.logit.config.ConfigurationManager;
import io.github.lucaseasedup.logit.config.InvalidPropertyValueException;
import io.github.lucaseasedup.logit.config.PredefinedConfiguration;
import io.github.lucaseasedup.logit.util.ExceptionHandler;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;

import multiengine.org.bukkit.configuration.InvalidConfigurationException;

import com.google.common.io.Files;

public final class BungeeLogItCore implements ICore
{
	private BungeeLogItCore(BungeeLogItPlugin plugin)
	{
		assert plugin != null;

		this.plugin = plugin;
		Core.setCore(this);
	}

	@Override
	public CancelledState start() throws FatalReportedException
	{
		if (isStarted())
			throw new IllegalStateException(
					"The LogIt core has already been started.");

		firstRun = !getDataFolder().exists();
		getDataFolder().mkdir();
		
		setUpConfiguration();
		
		channelManager = new ChannelServer();
		
		started = true;

		return CancelledState.NOT_CANCELLED;
	}
	
	private void setUpConfiguration() throws FatalReportedException
	{
		String configHeader = 
				  "# # # # # # # # # # # # # # # # # # # # #\n"
				+ "# BungeeCord LogIt Configuration File   #\n"
				+ "# # # # # # # # # # # # # # # # # # # # #\n";

		File oldConfigDefFile = getDataFile("config-def.b64");
		
		if (oldConfigDefFile.exists())
		{
			File newConfigDefFile = getDataFile(".doNotTouch/config-def.b64");

			try
			{
				newConfigDefFile.getParentFile().mkdirs();
				newConfigDefFile.createNewFile();

				Files.copy(oldConfigDefFile, newConfigDefFile);

				oldConfigDefFile.delete();
			}
			catch (IOException ex)
			{
				ExceptionHandler.handleException(ex);
			}
		}

		configurationManager = new ConfigurationManager();
		configurationManager.registerConfiguration("config.yml",
				".doNotTouch/config-def.b64", "bungee-config-def.ini", configHeader);

		try
		{
			configurationManager.loadAll();
		}
		catch (IOException | InvalidConfigurationException ex)
		{
			log(Level.SEVERE, "Could not load a configuration file.", ex);

			FatalReportedException.throwNew(ex);
		}
		catch (InvalidPropertyValueException ex)
		{
			log(Level.SEVERE, ex.getMessage());

			FatalReportedException.throwNew(ex);
		}
	}

	@Override
	public void stop()
	{
		if (!isStarted())
			throw new IllegalStateException("The LogIt core is not started.");
		
		channelManager.stop();
	}
	
	@Override
	public void restart() throws FatalReportedException
	{
		if (!isStarted())
			throw new IllegalStateException("The LogIt core is not started.");
		
		stop();
		
		start();
	}

	@Override
	public void log(Level level, Throwable throwable)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public ConfigurationManager getConfigurationManager()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PredefinedConfiguration getConfig(String filename)
	{
		// TODO Auto-generated method stub
		return null;
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
	
	public void log(Level level, String msg)
	{
		if (getLogger() == null)
		{
			getPlugin().getLogger().log(level, ChatColor.stripColor(msg));
		}
		else
		{
			getLogger().log(level, msg);
		}
	}

	public void log(Level level, String msg, Throwable throwable)
	{
		if (getLogger() == null)
		{
			getPlugin().getLogger().log(level, ChatColor.stripColor(msg),
					throwable);
		}
		else
		{
			getLogger().log(level, msg, throwable);
		}
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

	private ConfigurationManager configurationManager;
	private ChannelServer channelManager;
	private final BungeeLogItPlugin plugin;
	
	private boolean firstRun;
	private boolean started = false;
}

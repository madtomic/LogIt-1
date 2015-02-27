package io.github.lucaseasedup.logit.bungee;

import io.github.lucaseasedup.logit.common.FatalReportedException;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLogItPlugin extends Plugin
{
	@Override
	public void onEnable()
	{
		instance = this;
		
		core = BungeeLogItCore.getInstance();
		
		try
		{
			core.start();
		}
		catch (FatalReportedException e)
		{
		}
	}
	
	@Override
	public void onDisable()
	{
		core.stop();
	}
	
	public static BungeeLogItPlugin getInstance()
	{
		return instance;
	}
	
	private static BungeeLogItPlugin instance;
	private BungeeLogItCore core;
}

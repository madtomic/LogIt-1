package io.github.lucaseasedup.logit.bungee;

import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLogItPlugin extends Plugin
{
	@Override
	public void onEnable()
	{
		instance = this;
		
		core = BungeeLogItCore.getInstance();
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	public static BungeeLogItPlugin getInstance()
	{
		return instance;
	}
	
	private static BungeeLogItPlugin instance;
	private BungeeLogItCore core;
}

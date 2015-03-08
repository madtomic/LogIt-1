package io.github.lucaseasedup.logit;

public class Plugin
{
	public static IPlugin getPlugin()
	{
		return plugin;
	}
	
	public static void setPlugin(IPlugin plugin)
	{
		Plugin.plugin = plugin;
	}
	
	private static IPlugin plugin;
}

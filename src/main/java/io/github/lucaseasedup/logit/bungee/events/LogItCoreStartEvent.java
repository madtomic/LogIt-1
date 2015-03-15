package io.github.lucaseasedup.logit.bungee.events;

import io.github.lucaseasedup.logit.bungee.BungeeLogItCore;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class LogItCoreStartEvent extends Event implements Cancellable
{
	public LogItCoreStartEvent(BungeeLogItCore core)
	{
		this.core = core;
	}

	public BungeeLogItCore getCore()
	{
		return core;
	}

	@Override
	public boolean isCancelled()
	{
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean cancelled)
	{
		this.isCancelled = cancelled;
	}

	private boolean isCancelled;
	private BungeeLogItCore core;
}

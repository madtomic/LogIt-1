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
package io.github.lucaseasedup.logit.bukkit;

import io.github.lucaseasedup.logit.common.CancellableEvent;

import org.bukkit.event.HandlerList;

public final class LogItCoreStartEvent extends CancellableEvent
{
	/* package */LogItCoreStartEvent(LogItCore core)
	{
		if (core == null)
			throw new IllegalArgumentException();

		this.core = core;
	}

	public LogItCore getCore()
	{
		return core;
	}

	@Override
	public HandlerList getHandlers()
	{
		return handlers;
	}

	public static HandlerList getHandlerList()
	{
		return handlers;
	}

	private static final HandlerList handlers = new HandlerList();

	private final LogItCore core;
}

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

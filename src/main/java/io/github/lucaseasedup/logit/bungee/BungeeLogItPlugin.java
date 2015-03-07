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

import io.github.lucaseasedup.logit.common.FatalReportedException;
import io.github.lucaseasedup.logit.util.ExceptionHandler;
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
			ExceptionHandler.handleException(e);
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

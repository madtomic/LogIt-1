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
package io.github.lucaseasedup.logit;

import java.io.File;
import java.util.logging.Level;

public abstract class UniversalLogItCoreObject
{
	public UniversalLogItCoreObject()
	{
		core = Core.getCore();

		if (core == null)
			throw new IllegalStateException("No LogItCore instance found.");
	}
	
	protected ICore getCore()
	{
		return core;
	}
	
	protected final void log(Level level, String msg)
	{
		getCore().log(level, msg);
	}

	protected final void log(Level level, String msg, Throwable throwable)
	{
		getCore().log(level, msg, throwable);
	}

	protected final void log(Level level, Throwable throwable)
	{
		getCore().log(level, throwable);
	}
	
	protected final boolean isCoreStarted()
	{
		return getCore().isStarted();
	}

	protected final File getDataFolder()
	{
		return getCore().getDataFolder();
	}
	
	protected final File getDataFile(String path)
	{
		return getCore().getDataFile(path);
	}
	
	private ICore core;
}

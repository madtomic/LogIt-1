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

import io.github.lucaseasedup.logit.common.CancelledState;
import io.github.lucaseasedup.logit.common.FatalReportedException;
import io.github.lucaseasedup.logit.config.ConfigurationManager;
import io.github.lucaseasedup.logit.config.PredefinedConfiguration;

import java.io.File;
import java.util.logging.Level;

public interface ICore
{
	public CancelledState start() throws FatalReportedException;
	public void stop();
	public void restart() throws FatalReportedException;
	public void log(Level level, String msg);
	public void log(Level level, String msg, Throwable throwable);
	public void log(Level level, Throwable throwable);
	public File getDataFolder();
	public File getDataFile(String path);
	public String getVersion();
	public boolean isFirstRun();
	public boolean isStarted();
	public ConfigurationManager getConfigurationManager();
	public PredefinedConfiguration getConfig(String filename);
}

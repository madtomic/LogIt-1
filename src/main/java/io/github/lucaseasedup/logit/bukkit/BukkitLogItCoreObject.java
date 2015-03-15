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

import io.github.lucaseasedup.logit.UniversalLogItCoreObject;
import io.github.lucaseasedup.logit.account.AccountKeys;
import io.github.lucaseasedup.logit.account.AccountManager;
import io.github.lucaseasedup.logit.backup.BackupManager;
import io.github.lucaseasedup.logit.command.BukkitLogItTabCompleter;
import io.github.lucaseasedup.logit.common.Disposable;
import io.github.lucaseasedup.logit.config.ConfigurationManager;
import io.github.lucaseasedup.logit.config.PredefinedConfiguration;
import io.github.lucaseasedup.logit.cooldown.CooldownManager;
import io.github.lucaseasedup.logit.locale.LocaleManager;
import io.github.lucaseasedup.logit.message.BukkitLogItMessageDispatcher;
import io.github.lucaseasedup.logit.persistence.PersistenceManager;
import io.github.lucaseasedup.logit.profile.ProfileManager;
import io.github.lucaseasedup.logit.security.GlobalPasswordManager;
import io.github.lucaseasedup.logit.security.SecurityHelper;
import io.github.lucaseasedup.logit.session.SessionManager;

/**
 * Provides a convenient way for objects to interact with the LogIt core.
 */
public abstract class BukkitLogItCoreObject extends UniversalLogItCoreObject implements Disposable
{
	/**
	 * Constructs a new {@code LogItCoreObject}.
	 * 
	 * @throws IllegalStateException
	 *             if no {@code LogItCore} instance could be found.
	 */
	public BukkitLogItCoreObject()
	{
		core = BukkitLogItCore.getInstance();

		if (core == null)
		{
			throw new IllegalStateException("No LogItCore instance found.");
		}
	}

	@Override
	public void dispose()
	{
		// Left for optional implementation by extending classes.
	}

	@Override
	protected final BukkitLogItCore getCore()
	{
		return core;
	}

	protected final BukkitLogItPlugin getPlugin()
	{
		return getCore().getPlugin();
	}

	protected final ConfigurationManager getConfigurationManager()
	{
		return getCore().getConfigurationManager();
	}

	protected final PredefinedConfiguration getConfig(String filename)
	{
		return getCore().getConfig(filename);
	}

	protected final LocaleManager getLocaleManager()
	{
		return getCore().getLocaleManager();
	}

	protected final AccountManager getAccountManager()
	{
		return getCore().getAccountManager();
	}

	protected final AccountKeys keys()
	{
		return getCore().getAccountManager().getKeys();
	}

	protected final PersistenceManager getPersistenceManager()
	{
		return getCore().getPersistenceManager();
	}

	protected final SecurityHelper getSecurityHelper()
	{
		return getCore().getSecurityHelper();
	}

	protected final BackupManager getBackupManager()
	{
		return getCore().getBackupManager();
	}

	protected final SessionManager getSessionManager()
	{
		return getCore().getSessionManager();
	}

	protected final BukkitLogItMessageDispatcher getMessageDispatcher()
	{
		return getCore().getMessageDispatcher();
	}

	protected final BukkitLogItTabCompleter getTabCompleter()
	{
		return getCore().getTabCompleter();
	}

	protected final ProfileManager getProfileManager()
	{
		return getCore().getProfileManager();
	}

	protected final GlobalPasswordManager getGlobalPasswordManager()
	{
		return getCore().getGlobalPasswordManager();
	}

	protected final CooldownManager getCooldownManager()
	{
		return getCore().getCooldownManager();
	}

	private final BukkitLogItCore core;
}

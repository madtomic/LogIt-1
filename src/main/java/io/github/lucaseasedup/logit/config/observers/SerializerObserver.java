/*
 * SerializerObserver.java
 *
 * Copyright (C) 2012-2014 LucasEasedUp
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
package io.github.lucaseasedup.logit.config.observers;

import io.github.lucaseasedup.logit.account.Account;
import io.github.lucaseasedup.logit.config.Property;
import io.github.lucaseasedup.logit.config.PropertyObserver;
import io.github.lucaseasedup.logit.persistence.AirBarSerializer;
import io.github.lucaseasedup.logit.persistence.ExperienceSerializer;
import io.github.lucaseasedup.logit.persistence.HealthBarSerializer;
import io.github.lucaseasedup.logit.persistence.HungerBarSerializer;
import io.github.lucaseasedup.logit.persistence.LocationSerializer;
import io.github.lucaseasedup.logit.persistence.PersistenceSerializer;
import java.util.Arrays;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class SerializerObserver extends PropertyObserver
{
	@Override
	public void update(Property p)
	{
		Class<? extends PersistenceSerializer> clazz = null;

		switch (p.getPath())
		{
			case "waitingRoom.enabled":
				clazz = LocationSerializer.class;
				break;

			case "forceLogin.obfuscate.air":
				clazz = AirBarSerializer.class;
				break;

			case "forceLogin.obfuscate.health":
				clazz = HealthBarSerializer.class;
				break;

			case "forceLogin.obfuscate.experience":
				clazz = ExperienceSerializer.class;
				break;

			case "forceLogin.obfuscate.hunger":
				clazz = HungerBarSerializer.class;
				break;
		}

		if (clazz != null)
		{
			if (p.getBoolean())
			{
				enableSerializer(clazz);
			}
			else
			{
				disableSerializer(clazz);
			}
		}
	}

	private void enableSerializer(Class<? extends PersistenceSerializer> clazz)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (getSessionManager().isSessionAlive(player))
				continue;

			Account account = getAccountManager().selectAccount(
					player.getName(),
					Arrays.asList(keys().username(), keys().persistence()));

			if (account != null)
			{
				getPersistenceManager().serializeUsing(account, player, clazz);
			}
		}

		try
		{
			getPersistenceManager().registerSerializer(clazz);
		}
		catch (ReflectiveOperationException ex)
		{
			log(Level.WARNING,
					"Could not register serializer: " + clazz.getSimpleName(),
					ex);
		}
	}

	private void disableSerializer(Class<? extends PersistenceSerializer> clazz)
	{
		for (Player player : Bukkit.getOnlinePlayers())
		{
			Account account = getAccountManager().selectAccount(
					player.getName(),
					Arrays.asList(keys().username(), keys().persistence()));

			if (account != null)
			{
				getPersistenceManager()
						.unserializeUsing(account, player, clazz);
			}
		}

		getPersistenceManager().unregisterSerializer(clazz);
	}
}

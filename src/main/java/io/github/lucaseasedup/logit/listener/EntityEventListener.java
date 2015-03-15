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
package io.github.lucaseasedup.logit.listener;

import io.github.lucaseasedup.logit.bukkit.BukkitLogItCoreObject;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public final class EntityEventListener extends BukkitLogItCoreObject implements
		Listener
{
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onDamageIn(EntityDamageEvent event)
	{
		if (!getConfig("config.yml").getBoolean("forceLogin.prevent.damageIn")
				|| !(event.getEntity() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getEntity();

		if (!getSessionManager().isSessionAlive(player)
				&& getCore().isPlayerForcedToLogIn(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onDamageOut(EntityDamageByEntityEvent event)
	{
		if (!getConfig("config.yml").getBoolean("forceLogin.prevent.damageOut")
				|| !(event.getDamager() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getDamager();

		if (!getSessionManager().isSessionAlive(player)
				&& getCore().isPlayerForcedToLogIn(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onRegainHealth(EntityRegainHealthEvent event)
	{
		if (!getConfig("config.yml").getBoolean(
				"forceLogin.prevent.regainHealth")
				|| !(event.getEntity() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getEntity();

		if (!getSessionManager().isSessionAlive(player)
				&& getCore().isPlayerForcedToLogIn(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onFoodLevelChange(FoodLevelChangeEvent event)
	{
		if (!getConfig("config.yml").getBoolean(
				"forceLogin.prevent.foodLevelChange")
				|| !(event.getEntity() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getEntity();

		if (!getSessionManager().isSessionAlive(player)
				&& getCore().isPlayerForcedToLogIn(player))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	private void onEntityTarget(EntityTargetEvent event)
	{
		if (!getConfig("config.yml").getBoolean(
				"forceLogin.prevent.entityTarget")
				|| !(event.getTarget() instanceof Player))
		{
			return;
		}

		Player player = (Player) event.getTarget();

		if (!getSessionManager().isSessionAlive(player)
				&& getCore().isPlayerForcedToLogIn(player))
		{
			event.setCancelled(true);
		}
	}
}

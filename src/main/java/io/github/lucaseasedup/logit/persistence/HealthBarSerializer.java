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
package io.github.lucaseasedup.logit.persistence;

import java.util.Map;
import org.bukkit.entity.Player;

@Keys({ @Key(name = "health", constraint = KeyConstraint.NOT_EMPTY), })
public final class HealthBarSerializer implements PersistenceSerializer
{
	@Override
	public void serialize(Map<String, String> data, Player player)
	{
		String health = String.valueOf(player.getHealth());

		data.put("health", health);

		if (player.isOnline())
		{
			player.setHealth(player.getMaxHealth());
		}
	}

	@Override
	public void unserialize(Map<String, String> data, Player player)
	{
		String health = data.get("health");

		if (health != null)
		{
			if (player.isOnline())
			{
				player.setHealth(Double.valueOf(health).intValue());
			}
		}
	}
}

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
package io.github.lucaseasedup.logit.craftreflect.v1_7_R1;

import org.bukkit.entity.Player;

public final class CraftPlayer extends
		io.github.lucaseasedup.logit.craftreflect.CraftPlayer
{
	protected CraftPlayer(Player player)
	{
		super(player);
	}

	@Override
	public EntityPlayer getHandle()
	{
		return new EntityPlayer(getThis().getHandle());
	}

	private org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer getThis()
	{
		return (org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer) getHolder()
				.get();
	}
}

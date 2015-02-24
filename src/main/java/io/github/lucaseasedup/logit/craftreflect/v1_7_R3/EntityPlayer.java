/*
 * EntityPlayer.java
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
package io.github.lucaseasedup.logit.craftreflect.v1_7_R3;

public final class EntityPlayer extends
		io.github.lucaseasedup.logit.craftreflect.EntityPlayer
{
	protected EntityPlayer(Object o)
	{
		super(o);
	}

	@Override
	public int getPing()
	{
		return getThis().ping;
	}

	private net.minecraft.server.v1_7_R3.EntityPlayer getThis()
	{
		return (net.minecraft.server.v1_7_R3.EntityPlayer) getHolder().get();
	}
}

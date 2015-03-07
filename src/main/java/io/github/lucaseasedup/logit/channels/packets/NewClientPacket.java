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
package io.github.lucaseasedup.logit.channels.packets;

import java.util.ArrayList;
import java.util.List;

public class NewClientPacket implements IPacket
{
	private String logItVersion;
	
	public NewClientPacket()
	{
		this.logItVersion = "Unknown";
	}
	
	public NewClientPacket(String logItVersion)
	{
		this.logItVersion = logItVersion;
	}
	
	public String getVersion()
	{
		return this.logItVersion;
	}
	
	public void setVersion(String logItVersion)
	{
		this.logItVersion = logItVersion;
	}
	
	@Override
	public IPacket decode(List<String> message)
	{
		return new NewClientPacket(message.get(0));
	}

	@Override
	public List<String> encode()
	{
		List<String> temp = new ArrayList<String>(2);
		temp.add("NewClientPacket");
		temp.add(logItVersion);
		return temp;
	}
}

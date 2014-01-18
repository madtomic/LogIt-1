/*
 * AccountCreateEvent.java
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
package io.github.lucaseasedup.logit.account;

import java.util.Map;

public final class AccountCreateEvent extends AccountEvent
{
    public AccountCreateEvent(Map<String, String> properties)
    {
        this.properties = properties;
    }
    
    /**
     * Equal to <code>getProperty("logit.accounts.username")</code>.
     * 
     * @return the username.
     */
    public String getUsername()
    {
        return properties.get("logit.accounts.username");
    }
    
    public String getProperty(String name)
    {
        return properties.get(name);
    }
    
    private final Map<String, String> properties;
}

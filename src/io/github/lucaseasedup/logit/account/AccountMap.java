/*
 * AccountMap.java
 *
 * Copyright (C) 2012-2013 LucasEasedUp
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

import io.github.lucaseasedup.logit.db.Table;
import io.github.lucaseasedup.logit.db.WhereClause;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author LucasEasedUp
 */
public class AccountMap
{
    public AccountMap(Table table, Map<String, Account> initialData)
    {
        if (table == null)
            throw new NullPointerException();
        
        this.data = new HashMap<>(initialData);
        this.table = table;
    }
    
    public Account get(String username)
    {
        return data.get(username.toLowerCase());
    }
    
    public Account put(String username, Account account) throws SQLException
    {
        Map<String, String> properties = account.getAllProperties();
        String[] columns = new String[properties.size()];
        String[] values = new String[properties.size()];
        
        int i = 0;
        for (Entry<String, String> property : properties.entrySet())
        {
            columns[i] = property.getKey();
            values[i] = property.getValue();
            i++;
        }
        
        table.insert(columns, values);
        
        return data.put(username.toLowerCase(), account);
    }
    
    public void remove(String username) throws SQLException
    {
        table.delete(new WhereClause[]{
            new WhereClause("logit.accounts.username", WhereClause.EQUAL, username.toLowerCase())
        });
        
        data.remove(username.toLowerCase());
    }
    
    public Set<String> keySet()
    {
        return data.keySet();
    }
    
    public Set<Entry<String, Account>> entrySet()
    {
        return data.entrySet();
    }
    
    public Collection<Account> values()
    {
        return data.values();
    }
    
    public int size()
    {
        return data.size();
    }
    
    public boolean containsKey(String username)
    {
        for (String s : data.keySet())
        {
            if (s.equalsIgnoreCase((String) username))
            {
                return true;
            }
        }
        
        return false;
    }
    
    private final Map<String, Account> data;
    private final Table table;
}

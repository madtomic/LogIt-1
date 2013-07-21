/*
 * InventorySerializationException.java
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
package io.github.lucaseasedup.logit.inventory;

/**
 * @author LucasEasedUp
 */
public class InventorySerializationException extends Exception
{
    /**
     * Creates a new instance of
     * <code>InventorySerializationException</code> without detail message.
     */
    public InventorySerializationException()
    {
    }
    
    /**
     * Constructs an instance of
     * <code>InventorySerializationException</code> with the specified detail message.
     * <p/>
     * @param msg the detail message.
     */
    public InventorySerializationException(String msg)
    {
        super(msg);
    }
    
    public InventorySerializationException(Throwable cause)
    {
        super(cause);
    }
}

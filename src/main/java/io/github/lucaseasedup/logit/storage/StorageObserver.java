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
package io.github.lucaseasedup.logit.storage;

import io.github.lucaseasedup.logit.storage.Storage.DataType;
import java.util.Hashtable;

public abstract class StorageObserver
{
	public void beforeClose()
	{
	}

	@SuppressWarnings("unused")
	public void afterCreateUnit(String unit, Hashtable<String, DataType> keys)
	{
	}

	@SuppressWarnings("unused")
	public void afterRenameUnit(String unit, String newName)
	{
	}

	@SuppressWarnings("unused")
	public void afterEraseUnit(String unit)
	{
	}

	@SuppressWarnings("unused")
	public void afterRemoveUnit(String unit)
	{
	}

	@SuppressWarnings("unused")
	public void afterAddKey(String unit, String key, DataType type)
	{
	}

	@SuppressWarnings("unused")
	public void afterAddEntry(String unit, Storage.Entry entry)
	{
	}

	@SuppressWarnings("unused")
	public void afterUpdateEntries(String unit, Storage.Entry entrySubset,
			Selector selector)
	{
	}

	@SuppressWarnings("unused")
	public void afterRemoveEntries(String unit, Selector selector)
	{
	}
}

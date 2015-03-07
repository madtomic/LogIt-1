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

import io.github.lucaseasedup.logit.util.org.apache.tools.ant.util.LinkedHashtable;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public final class CsvStorage extends Storage
{
	public CsvStorage(File dir)
	{
		if (dir == null)
			throw new IllegalArgumentException();

		this.dir = dir;
	}

	@Override
	public void connect() throws IOException
	{
		connected = true;
	}

	@Override
	public boolean isConnected() throws IOException
	{
		return connected;
	}

	@Override
	public void ping() throws IOException
	{
	}

	@Override
	public void close() throws IOException
	{
		connected = false;
	}

	@Override
	public List<String> getUnitNames() throws IOException
	{
		File[] files = dir.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return pathname.isFile();
			}
		});

		List<String> units = new LinkedList<>();

		for (File file : files)
		{
			units.add(file.getName());
		}

		return units;
	}

	@Override
	public Hashtable<String, DataType> getKeys(String unit) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		Hashtable<String, DataType> keys = new LinkedHashtable<>();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				dir, unit))))
		{
			String line = br.readLine();

			if (line == null)
				throw new IOException("Null line.");

			String[] topValues = line.split(",");

			for (int i = 0; i < topValues.length; i++)
			{
				keys.put(unescapeValue(topValues[i]), DataType.TEXT);
			}
		}

		return keys;
	}

	@Override
	public String getPrimaryKey(String unit) throws IOException
	{
		return null;
	}

	@Override
	public List<Storage.Entry> selectEntries(String unit) throws IOException
	{
		return selectEntries(unit, null, new SelectorConstant(true));
	}

	@Override
	public List<Storage.Entry> selectEntries(String unit, List<String> keys)
			throws IOException
	{
		return selectEntries(unit, keys, new SelectorConstant(true));
	}

	@Override
	public List<Storage.Entry> selectEntries(String unit, Selector selector)
			throws IOException
	{
		return selectEntries(unit, null, selector);
	}

	@Override
	public List<Storage.Entry> selectEntries(String unit, List<String> keys,
			Selector selector) throws IOException
	{
		List<Storage.Entry> entries = new ArrayList<>();

		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				dir, unit))))
		{
			String line = br.readLine();

			if (line == null)
				throw new IOException("Null line.");

			String[] tableKeys = line.split(",");

			for (int i = 0; i < tableKeys.length; i++)
			{
				tableKeys[i] = unescapeValue(tableKeys[i]);
			}

			while ((line = br.readLine()) != null)
			{
				StringBuilder lineBuilder = new StringBuilder(line);

				while (!line.endsWith("\""))
				{
					line = br.readLine();

					if (line == null)
						throw new IOException("Corrupted CSV file.");

					lineBuilder.append("\r\n");
					lineBuilder.append(line);
				}

				String[] lineValues = lineBuilder.toString().split(
						"(?<=\"),(?=\")");
				Storage.Entry.Builder entryBuilder = new Storage.Entry.Builder();

				for (int i = 0; i < lineValues.length; i++)
				{
					if (keys == null || keys.contains(tableKeys[i]))
					{
						entryBuilder.put(tableKeys[i],
								unescapeValue(lineValues[i]));
					}
				}

				Storage.Entry entry = entryBuilder.build();

				if (SqlUtils.resolveSelector(selector, entry))
				{
					entries.add(entry);
				}
			}
		}

		return entries;
	}

	@Override
	public void createUnit(String unit, Hashtable<String, DataType> keys,
			String primaryKey) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		if (primaryKey != null && !keys.containsKey(primaryKey))
			throw new IllegalArgumentException(
					"Cannot create index on a non-existing key");

		File file = new File(dir, unit);

		if (file.exists())
			return;

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file)))
		{
			StringBuilder sb = new StringBuilder();

			for (String key : keys.keySet())
			{
				if (sb.length() > 0)
					sb.append(",");

				sb.append(escapeValue(key));
			}

			sb.append("\r\n");
			bw.write(sb.toString());
		}
	}

	@Override
	public void renameUnit(String unit, String newName) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		new File(dir, unit).renameTo(new File(dir, newName));
	}

	@Override
	public void eraseUnit(String unit) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		String keys;

		try (BufferedReader bw = new BufferedReader(new FileReader(new File(
				dir, unit))))
		{
			keys = bw.readLine();
		}

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				dir, unit))))
		{
			bw.write(keys + "\r\n");
		}
	}

	@Override
	public void removeUnit(String unit) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		new File(dir, unit).delete();
	}

	@Override
	public void addKey(String unit, String key, DataType type)
			throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		Hashtable<String, DataType> keys = getKeys(unit);
		String primaryKey = getPrimaryKey(unit);

		if (keys.containsKey(key))
			throw new IOException("Key with this name already exists: " + key);

		List<Storage.Entry> entries = selectEntries(unit);

		keys.put(key, type);
		removeUnit(unit);
		createUnit(unit, keys, primaryKey);

		for (Storage.Entry entry : entries)
		{
			addEntry(unit, entry);
		}
	}

	@Override
	public void addEntry(String unit, Storage.Entry entry) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		Hashtable<String, DataType> keys = getKeys(unit);

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(new File(
				dir, unit), true)))
		{
			StringBuilder sb = new StringBuilder();

			for (String key : keys.keySet())
			{
				if (sb.length() > 0)
					sb.append(",");

				String value = entry.get(key);

				if (value != null && !value.isEmpty())
				{
					sb.append(escapeValue(value));
				}
				else
				{
					sb.append("\"\"");
				}
			}

			sb.append("\r\n");
			bw.write(sb.toString());
		}
	}

	@Override
	public void updateEntries(String unit, Storage.Entry entrySubset,
			Selector selector) throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		Hashtable<String, DataType> keys = getKeys(unit);
		String primaryKey = getPrimaryKey(unit);

		List<Storage.Entry> entries = selectEntries(unit);

		removeUnit(unit);
		createUnit(unit, keys, primaryKey);

		for (Storage.Entry entry : entries)
		{
			if (SqlUtils.resolveSelector(selector, entry))
			{
				for (Storage.Entry.Datum datum : entrySubset)
				{
					entry.put(datum.getKey(), datum.getValue());
				}
			}

			addEntry(unit, entry);
		}
	}

	@Override
	public void removeEntries(String unit, Selector selector)
			throws IOException
	{
		if (!connected)
			throw new IOException("Database closed.");

		Hashtable<String, DataType> keys = getKeys(unit);
		String primaryKey = getPrimaryKey(unit);

		List<Storage.Entry> entries = selectEntries(unit);

		removeUnit(unit);
		createUnit(unit, keys, primaryKey);

		for (Storage.Entry entry : entries)
		{
			if (!SqlUtils.resolveSelector(selector, entry))
			{
				addEntry(unit, entry);
			}
		}
	}

	@Override
	public void executeBatch() throws IOException
	{
		// Batching is not supported.
	}

	@Override
	public void clearBatch() throws IOException
	{
		// Batching is not supported.
	}

	private String escapeValue(String s)
	{
		s = s.replace(",", "\\,");

		return "\"" + s + "\"";
	}

	private String unescapeValue(String s)
	{
		if (s == null)
			throw new IllegalArgumentException();

		s = s.trim();
		s = s.replace("\\,", ",");

		if (s.startsWith("\""))
		{
			s = s.substring(1);
		}

		if (s.endsWith("\""))
		{
			s = s.substring(0, s.length() - 1);
		}

		return s;
	}

	private final File dir;
	private boolean connected = false;
}

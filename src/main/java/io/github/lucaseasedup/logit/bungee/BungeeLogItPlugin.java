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
package io.github.lucaseasedup.logit.bungee;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import multiengine.org.bukkit.ChatColor;
import io.github.lucaseasedup.logit.IPlugin;
import io.github.lucaseasedup.logit.common.FatalReportedException;
import io.github.lucaseasedup.logit.util.ExceptionHandler;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeLogItPlugin extends Plugin implements IPlugin
{
	@Override
	public void onEnable()
	{
		instance = this;
		
		core = BungeeLogItCore.getInstance();
		
		try
		{
			core.start();
		}
		catch (FatalReportedException e)
		{
			ExceptionHandler.handleException(e);
		}
	}
	
	@Override
	public void onDisable()
	{
		core.stop();
	}
	
	/**
	 * Reloads message files.
	 * <p>
	 * First, it tries to load local <i>messages_{prefix}.properties</i> from the plugin JAR. If the file does not exist, it tries to load global <i>messages.properties</i>. If this fails too,
	 * {@code FileNotFoundException} will be thrown.
	 * <p>
	 * This method will also try to load custom global/local message files from the <i>lang</i> directory if present, that will be merged with built-in message files.
	 * 
	 * @param prefix
	 *            the locale prefix.
	 * @throws IOException
	 *             if there was an error while reading.
	 */
	public void reloadMessages(String prefix) throws IOException
	{
		loadMessages(prefix);
	}

	private void loadMessages(String prefix) throws IOException
	{
		if (prefix == null)
			throw new IllegalArgumentException("Prefix cannot be null");

		messages = null;

		try (JarFile jarFile = new JarFile(getFile()))
		{
			JarEntry jarEntry = jarFile.getJarEntry("messages_" + prefix
					+ ".properties");

			if (jarEntry == null)
			{
				jarEntry = jarFile.getJarEntry("messages.properties");
			}

			if (jarEntry == null)
				throw new FileNotFoundException("No message file found.");

			InputStream messagesInputStream = jarFile.getInputStream(jarEntry);

			try (Reader messagesReader = new InputStreamReader(
					messagesInputStream, "UTF-8"))
			{
				messages = new PropertyResourceBundle(messagesReader);
			}
		}

		loadCustomGlobalMessages("lang/messages.properties");
		loadCustomLocalMessages("lang/messages_" + prefix + ".properties");
	}

	private void loadCustomGlobalMessages(String path) throws IOException
	{
		File file = new File(getDataFolder(), path);

		if (!file.exists())
		{
			customGlobalMessages = null;

			return;
		}

		try (InputStream is = new FileInputStream(file))
		{
			customGlobalMessages = new PropertyResourceBundle(is);
		}
	}

	private void loadCustomLocalMessages(String path) throws IOException
	{
		File file = new File(getDataFolder(), path);

		if (!file.exists())
		{
			customLocalMessages = null;

			return;
		}

		try (InputStream is = new FileInputStream(file))
		{
			customLocalMessages = new PropertyResourceBundle(is);
		}
	}

	public String getMessage(String label)
	{
		if (label == null)
			throw new IllegalArgumentException();

		if (getInstance() == null)
			return label;

		if (getInstance().messages == null)
			return label;

		String message;

		try
		{
			message = getInstance().messages.getString(label);

			if (getInstance().customGlobalMessages != null
					&& getInstance().customGlobalMessages.containsKey(label))
			{
				message = getInstance().customGlobalMessages.getString(label);
			}

			if (getInstance().customLocalMessages != null
					&& getInstance().customLocalMessages.containsKey(label))
			{
				message = getInstance().customLocalMessages.getString(label);
			}
		}
		catch (MissingResourceException | ClassCastException ex)
		{
			return label;
		}

		message = ChatColor.translateAlternateColorCodes('&', message);

		return parseMessage(message);
	}

	private static String parseMessage(String message)
	{
		message = message
				.replace("%bungee_version%", BungeeCord.getInstance().getVersion());
		message = message.replace("%logit_version%", BungeeLogItPlugin.getInstance()
				.getDescription().getVersion());

		return message;
	}
	
	public static BungeeLogItPlugin getInstance()
	{
		return instance;
	}
	
	private static BungeeLogItPlugin instance;
	private BungeeLogItCore core;
	private PropertyResourceBundle messages;
	private PropertyResourceBundle customGlobalMessages;
	private PropertyResourceBundle customLocalMessages;
}

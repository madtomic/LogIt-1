/*
 * SessionManager.java
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
package io.github.lucaseasedup.logit.session;

import static io.github.lucaseasedup.logit.message.MessageHelper.sendMsg;
import static io.github.lucaseasedup.logit.message.MessageHelper.t;
import static io.github.lucaseasedup.logit.util.CollectionUtils.containsIgnoreCase;
import static io.github.lucaseasedup.logit.util.PlayerUtils.getPlayerIp;
import io.github.lucaseasedup.logit.CancelledState;
import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.config.TimeUnit;
import io.github.lucaseasedup.logit.hooks.BungeeHook;
import io.github.lucaseasedup.logit.storage.SqliteStorage;
import io.github.lucaseasedup.logit.storage.Storage;
import io.github.lucaseasedup.logit.storage.Storage.DataType;
import io.github.lucaseasedup.logit.util.PlayerUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Provides a facility manage login sessions.
 */
public final class SessionManager extends LogItCoreObject implements Runnable
{
	/**
	 * Do not call directly.
	 */
	@Override
	public void dispose()
	{
		if (sessions != null)
		{
			sessions.clear();
			sessions = null;
		}
	}

	/**
	 * Internal method. Do not call directly.
	 */
	@Override
	public void run()
	{
		for (Map.Entry<String, Session> entry : sessions.entrySet())
		{
			String username = entry.getKey();
			Session session = entry.getValue();
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayerExact(username);

			// Player is logged in, either online or offline.
			if (session.getStatus() >= 0L)
			{
				// If player is online.
				if (player != null)
				{
					session.setStatus(0L);

					if (automaticLogoutEnabled)
					{
						if (session.getInactivityTime() >= inactivityTimeToLogOut)
						{
							endSession(username);

							sendMsg(player, t("automaticallyLoggedOut"));

							if (getCore().isPlayerForcedToLogIn(player))
							{
								getMessageDispatcher().sendForceLoginMessage(
										player);
							}

							session.resetInactivityTime();
						}
						else
						{
							session.advanceInactivityTime(TASK_PERIOD);
						}
					}
				}
				else
				{
					destroySession(username);
				}
			}
			// Player is online but otherwise logged out.
			else if (player != null)
			{
				if (!containsIgnoreCase(username, disableTimeoutForPlayers)
						&& getCore().isPlayerForcedToLogIn(player))
				{
					if (timeoutEnabled && session.getStatus() <= -timeoutValue)
					{
						player.kickPlayer(t("forcedLoginTimeout"));
					}
					else
					{
						session.updateStatus(-TASK_PERIOD);
					}
				}
			}
			// Player is logged out and offline.
			else
			{
				destroySession(username);
			}
		}
	}

	/**
	 * Internal method. Do not use!
	 */
	public void updateAutologoutSettings()
	{
		timeoutEnabled = getConfig("config.yml").getBoolean(
				"forceLogin.timeout.enabled");

		timeoutValue = getConfig("config.yml").getTime(
				"forceLogin.timeout.value", TimeUnit.TICKS);

		disableTimeoutForPlayers = getConfig("config.yml").getStringList(
				"forceLogin.timeout.disableForPlayers");

		automaticLogoutEnabled = getConfig("config.yml").getBoolean(
				"automaticLogout.enabled");

		inactivityTimeToLogOut = getConfig("config.yml").getTime(
				"automaticLogout.inactivityTime", TimeUnit.TICKS);
	}

	/**
	 * Returns a {@code Session} object associated with a specific username.
	 * 
	 * @param username
	 *            The username.
	 * @return The {@code Session} object, or {@code null} if no session
	 *         is associated with this username.
	 */
	public Session getSession(String username)
	{
		return sessions.get(username.toLowerCase());
	}

	/**
	 * Returns a {@code Session} object associated with a specific player.
	 * 
	 * @param player
	 *            The player.
	 * @return The {@code Session} object, or {@code null} if no session
	 *         is associated with this player.
	 */
	public Session getSession(Player player)
	{
		return getSession(player.getName());
	}

	/**
	 * Checks whether the session associated with a specific username is alive.
	 *
	 * @param username
	 *            The username.
	 * @return {@code true} if such session exists, is alive and,
	 *         if a player with this username is online, player IP matches
	 *         session IP; {@code false} otherwise.
	 * @throws IllegalArgumentException
	 *             If {@code username} is {@code null}.
	 * @see #isSessionAlive(Player)
	 */
	public boolean isSessionAlive(String username)
	{
		if (username == null)
			throw new IllegalArgumentException();

		Session session = getSession(username);

		if (session == null)
			return false;

		if (PlayerUtils.isPlayerOnline(username))
		{
			@SuppressWarnings("deprecation")
			Player player = Bukkit.getPlayerExact(username);
			String ip = getPlayerIp(player);

			return session.isAlive() && ip.equals(session.getIp());
		}
		else
		{
			return session.isAlive();
		}
	}

	/**
	 * Checks whether a session associated with a specific player is alive.
	 *
	 * @param player
	 *            The player.
	 * @return {@code true} if {@code player} is not {@code null},
	 *         such session exists, is alive and player IP matches session IP; {@code false} otherwise.
	 */
	public boolean isSessionAlive(Player player)
	{
		if (player == null)
			return false;

		Session session = getSession(player);

		if (session == null)
			return false;

		String ip = getPlayerIp(player);

		return session.isAlive() && ip.equals(session.getIp());
	}

	/**
	 * Creates a new session and associates it with a specific player.
	 * <p>
	 * If a session for this player already exists, no action will be taken.
	 * <p>
	 * This method emits the {@code SessionCreateEvent} event.
	 *
	 * @param player
	 *            The player.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionCreateEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code player} is {@code null}.
	 */
	public CancelledState createSession(Player player)
	{
		if (player == null)
			throw new IllegalArgumentException();

		String username = player.getName().toLowerCase();
		String ip = getPlayerIp(player);

		if (getSession(player) != null)
			return CancelledState.NOT_CANCELLED;

		SessionEvent evt = new SessionCreateEvent(username);

		Bukkit.getPluginManager().callEvent(evt);

		if (evt.isCancelled())
			return CancelledState.CANCELLED;

		// Create session.
		Session session = new Session(ip);
		sessions.put(username, session);

		log(Level.FINE, t("createSession.success.log").replace("{0}", username));

		return CancelledState.NOT_CANCELLED;
	}

	/**
	 * Destroys a session associated with a specific username.
	 * <p>
	 * If no session for this username exists, no action will be taken. If the session exists and is alive, this method will try to end it before proceeding.
	 * <p>
	 * This method emits the {@code SessionDestroyEvent} event.
	 * 
	 * @param username
	 *            The username.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionDestroyEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code username} is {@code null}.
	 */
	public CancelledState destroySession(String username)
	{
		if (username == null)
			throw new IllegalArgumentException();

		Session session = getSession(username);

		if (session == null)
			return CancelledState.NOT_CANCELLED;

		if (session.isAlive())
		{
			endSession(username);
		}

		SessionEvent evt = new SessionDestroyEvent(username, session);

		Bukkit.getPluginManager().callEvent(evt);

		if (evt.isCancelled())
			return CancelledState.CANCELLED;

		sessions.remove(username.toLowerCase());

		log(Level.FINE,
				t("destroySession.success.log").replace("{0}",
						username.toLowerCase()));

		return CancelledState.NOT_CANCELLED;
	}

	/**
	 * Destroys a session associated with a specific player.
	 * <p>
	 * If no session for this player exists, no action will be taken. If the session exists and is alive, this method will try to end it before proceeding.
	 * <p>
	 * This method emits the {@code SessionDestroyEvent} event.
	 * 
	 * @param player
	 *            The player.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionDestroyEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code player} is {@code null}.
	 */
	public CancelledState destroySession(Player player)
	{
		if (player == null)
			throw new IllegalArgumentException();

		return destroySession(player.getName());
	}

	/**
	 * Starts a session associated with a specific player.
	 * <p>
	 * If the session has already been started (e.i. is alive), no action will be taken. If no session exists for this player, it will be created.
	 * <p>
	 * This method emits the {@code SessionStartEvent} event.
	 * 
	 * @param player
	 *            The player.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionStartEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code player} is {@code null}.
	 */
	public CancelledState startSession(Player player)
	{
		if (player == null)
			throw new IllegalArgumentException();

		String username = player.getName().toLowerCase();
		Session session = getSession(player);

		if (session == null)
		{
			createSession(player);

			session = getSession(player);
		}

		if (session.isAlive())
			return CancelledState.NOT_CANCELLED;

		SessionEvent evt = new SessionStartEvent(username, session);

		Bukkit.getPluginManager().callEvent(evt);

		if (evt.isCancelled())
			return CancelledState.CANCELLED;

		// Start the session.
		session.setStatus(0L);

		log(Level.FINE, t("startSession.success.log").replace("{0}", username));
		
		if (getConfig("config.yml").getBoolean("bungeecord.redirAfterLogin.enabled"))
		{
			BungeeHook.sendPlayerToServer(player, getConfig("config.yml").getString("bungeecord.redirAfterLogin.server"));
		}

		return CancelledState.NOT_CANCELLED;
	}

	/**
	 * Starts a session associated with a specific username.
	 * <p>
	 * If the session has already been started (e.i. is alive), no action will be taken. If no session exists for this username, it will be created.
	 * <p>
	 * This method emits the {@code SessionStartEvent} event.
	 * 
	 * @param username
	 *            The username.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionStartEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code player} is {@code null}.
	 * @deprecated Use {@link #startSession(Player)} instead.
	 */
	@Deprecated
	public CancelledState startSession(String username)
	{
		if (username == null)
			throw new IllegalArgumentException();

		Player player = PlayerUtils.getPlayer(username);

		if (player != null)
		{
			return startSession(player);
		}
		else
		{
			return CancelledState.NOT_CANCELLED;
		}
	}

	/**
	 * Ends a session associated with a specific username.
	 * <p>
	 * If the session is not alive or does not exist, no action will be taken.
	 * <p>
	 * This method emits the {@code SessionEndEvent} event.
	 *
	 * @param username
	 *            The username.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionEndEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code username} is {@code null}.
	 */
	public CancelledState endSession(String username)
	{
		if (username == null)
			throw new IllegalArgumentException();

		Session session = getSession(username);

		if (session == null)
			return CancelledState.NOT_CANCELLED;

		if (!session.isAlive())
			return CancelledState.NOT_CANCELLED;

		SessionEvent evt = new SessionEndEvent(username, session);

		Bukkit.getPluginManager().callEvent(evt);

		if (evt.isCancelled())
			return CancelledState.CANCELLED;

		// End the session.
		session.setStatus(-1L);

		log(Level.FINE,
				t("endSession.success.log").replace("{0}",
						username.toLowerCase()));

		return CancelledState.NOT_CANCELLED;
	}

	/**
	 * Ends a session associated with a specific player.
	 * <p>
	 * If the session is not alive or does not exist, no action will be taken.
	 * <p>
	 * This method emits the {@code SessionEndEvent} event.
	 *
	 * @param player
	 *            The player.
	 * @return A {@code CancellableState} indicating whether this operation
	 *         has been cancelled by one of the {@code SessionEndEvent} handlers.
	 * @throws IllegalArgumentException
	 *             If {@code player} is {@code null}.
	 */
	public CancelledState endSession(Player player)
	{
		if (player == null)
			throw new IllegalArgumentException();

		return endSession(player.getName());
	}

	/**
	 * Returns an iterator over the sessions in this {@code SessionManager}.
	 * <p>
	 * Element removal is not supported.
	 *
	 * @return The session iterator.
	 */
	public Iterator<Map.Entry<String, Session>> sessionIterator()
	{
		return newSessionIterator();
	}

	private Iterator<Map.Entry<String, Session>> newSessionIterator()
	{
		return new Iterator<Map.Entry<String, Session>>()
		{
			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public Map.Entry<String, Session> next()
			{
				return it.next();
			}

			@Override
			public boolean hasNext()
			{
				return it.hasNext();
			}

			private final Iterator<Map.Entry<String, Session>> it = sessions
					.entrySet().iterator();
		};
	}

	/**
	 * Exports all sessions from this {@code SessionManager} to a file.
	 * <p>
	 * The file will be deleted before exporting.
	 *
	 * @param file
	 *            The file to which the sessions will be exported.
	 * @throws IOException
	 *             If an I/O error occurred.
	 * @throws IllegalArgumentException
	 *             If {@code file} is {@code null}.
	 */
	public void exportSessions(File file) throws IOException
	{
		if (file == null)
			throw new IllegalArgumentException();

		file.delete();

		try (Storage sessionsStorage = new SqliteStorage("jdbc:sqlite:" + file))
		{
			sessionsStorage.connect();
			sessionsStorage.createUnit("sessions",
					new Hashtable<String, DataType>()
					{
						private static final long serialVersionUID = 1L;

						{
							put("username", DataType.TINYTEXT);
							put("status", DataType.INTEGER);
							put("ip", DataType.TINYTEXT);
						}
					}, "username");
			sessionsStorage.setAutobatchEnabled(true);

			for (Map.Entry<String, Session> e : sessions.entrySet())
			{
				sessionsStorage
						.addEntry(
								"sessions",
								new Storage.Entry.Builder()
										.put("username", e.getKey())
										.put("status",
												String.valueOf(e.getValue()
														.getStatus()))
										.put("ip", e.getValue().getIp())
										.build());
			}

			sessionsStorage.executeBatch();
			sessionsStorage.clearBatch();
			sessionsStorage.setAutobatchEnabled(false);
		}
	}

	/**
	 * Imports all sessions from a file to this {@code SessionManager}.
	 * <p>
	 * Only the sessions that don't exist in this {@code SessionManager} will be imported.
	 *
	 * @param file
	 *            The file from which the sessions will be imported.
	 * @throws FileNotFoundException
	 *             If no such file exists.
	 * @throws IOException
	 *             If an I/O error occurred.
	 * @throws IllegalArgumentException
	 *             If {@code file} is {@code null}.
	 */
	public void importSessions(File file) throws FileNotFoundException,
			IOException
	{
		if (file == null)
			throw new IllegalArgumentException();

		if (!file.exists())
			throw new FileNotFoundException();

		try (Storage sessionsStorage = new SqliteStorage("jdbc:sqlite:" + file))
		{
			sessionsStorage.connect();

			List<Storage.Entry> entries = sessionsStorage.selectEntries(
					"sessions", Arrays.asList("username", "status", "ip"));

			for (Storage.Entry entry : entries)
			{
				String username = entry.get("username");

				if (getSession(username) == null)
				{
					String ip = entry.get("ip");
					long status = Long.parseLong(entry.get("status"));

					Session session = new Session(ip);
					session.setStatus(status);
					sessions.put(username, session);
				}
			}
		}
	}

	/**
	 * Recommended task period of {@code SessionManager} running as a Bukkit task.
	 */
	public static final long TASK_PERIOD = TimeUnit.TICKS.convert(20,
			TimeUnit.TICKS);

	private Map<String, Session> sessions = new ConcurrentHashMap<>();

	/**
	 * Autologout settings
	 * Moved from run() for performance
	 */
	private boolean timeoutEnabled = getConfig("config.yml").getBoolean(
			"forceLogin.timeout.enabled");

	private long timeoutValue = getConfig("config.yml").getTime(
			"forceLogin.timeout.value", TimeUnit.TICKS);

	private List<String> disableTimeoutForPlayers = getConfig("config.yml")
			.getStringList("forceLogin.timeout.disableForPlayers");

	private boolean automaticLogoutEnabled = getConfig("config.yml")
			.getBoolean("automaticLogout.enabled");

	private long inactivityTimeToLogOut = getConfig("config.yml").getTime(
			"automaticLogout.inactivityTime", TimeUnit.TICKS);
}

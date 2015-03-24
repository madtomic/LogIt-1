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

package io.github.lucaseasedup.logit.security;

import io.github.lucaseasedup.logit.account.Account;
import io.github.lucaseasedup.logit.bukkit.BukkitLogItCore;
import io.github.lucaseasedup.logit.bukkit.BukkitLogItCoreObject;
import io.github.lucaseasedup.logit.util.org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public final class FacebookAuthManager extends BukkitLogItCoreObject
{
	public FacebookAuthManager()
	{
		playerskeys = new HashMap<>(15);
		thread = new SessionCheckerThread();
	}

	private class SessionCheckerThread extends Thread
	{
		protected SessionCheckerThread()
		{
			running = true;
			parametres.add(BukkitLogItCore.getInstance().getAccountManager().getKeys().fbid());
			this.start();
		}

		public void run()
		{
			while(running)
			{
				Iterator<String> i = playerskeys.keySet().iterator();
				while(i.hasNext())
				{
					String nick = i.next();
					Player player = Bukkit.getPlayerExact(nick);

					if(player == null)
					{
						playerskeys.remove(nick);
						continue;
					}

					if(BukkitLogItCore.getInstance().getSessionManager().isSessionAlive(player))
					{
						playerskeys.remove(nick);
						continue;
					}

					String response = "";

					try
					{
						StringBuilder url = new StringBuilder();
						url.append(getConfig("config.yml").getString("facebook.auth_url"));
						url.append("server-api/check_auth.php?serverId=");
						url.append(getConfig("config.yml").getString("facebook.server_key"));
						url.append("&key=");
						url.append(playerskeys.get(nick));
						HttpURLConnection conn = (HttpURLConnection) new URL(url.toString()).openConnection();
						conn.setRequestMethod("GET");

						response = IOUtils.toString(conn.getInputStream());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}

					String[] info = response.split(":");
					if(info.length != 2)
					{
						continue;
					}

					if(Integer.parseInt(info[0]) == 0)
					{
						continue;
					}

					if(BukkitLogItCore.getInstance().getAccountManager().selectAccount(nick, parametres).getFbId() == Integer.valueOf(info[1]))
					{
						//Zalogowac nooba
					}
					else
					{
						//Wywalic info, ze zle konto itd. itd.
					}
				}
			}
		}

		public void end()
		{
			if(!running)
				throw new IllegalStateException();
			running = false;
		}

		public boolean isRunning()
		{
			return running;
		}

		private boolean running;
		private final List<String> parametres = new ArrayList<>(1);
	}

	private Map<String, String> playerskeys;
	private SessionCheckerThread thread;
}

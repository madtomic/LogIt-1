package io.github.lucaseasedup.logit.hooks;

import com.gmail.bartlomiejkmazur.autoin.api.auth.EventPriority;
import com.gmail.bartlomiejkmazur.autoin.api.auth.BukkitLoginPlugin;
import io.github.lucaseasedup.logit.Core;
import io.github.lucaseasedup.logit.Plugin;
import io.github.lucaseasedup.logit.account.Account;
import io.github.lucaseasedup.logit.account.AccountManager;
import io.github.lucaseasedup.logit.bukkit.BukkitLogItCore;
import io.github.lucaseasedup.logit.bukkit.BukkitLogItPlugin;
import io.github.lucaseasedup.logit.session.SessionManager;
import io.github.lucaseasedup.logit.util.org.apache.commons.lang3.RandomStringUtils;
import org.bukkit.entity.Player;

public class AutoInPlugin extends BukkitLoginPlugin
{
	@Override
	public void forceLogin(Object o)
	{
		System.out.println("forceLogin");
		Player player = (Player)o;
		SessionManager sm = ((BukkitLogItCore) Core.getCore()).getSessionManager();
		if(sm.getSession(player) == null)
		{
			sm.createSession(player);
		}
		sm.startSession(player);
	}

	@Override
	public void forceRegister(final String nick)
	{
		AccountManager am = ((BukkitLogItCore) Core.getCore()).getAccountManager();
		final Account account = new Account(nick);
		account.changePassword(RandomStringUtils.random(12));
		account.setLastActiveDate(System.currentTimeMillis() / 1000L);
		account.setRegistrationDate(System.currentTimeMillis() / 1000L);
		am.insertAccount(account);
	}

	@Override
	public void forceLogout(Object o)
	{
		Player player = (Player)o;
		SessionManager sm = ((BukkitLogItCore) Core.getCore()).getSessionManager();
		if(sm.getSession(player) == null)
		{
			return;
		}
		sm.destroySession(player);
	}

	@Override
	public boolean needRegisterToLogin(final String nick)
	{
		System.out.println("needRegisterToLogin");
		return false;
	}

	@Override
	public boolean isLoggedIn(Object o)
	{
		return ((BukkitLogItCore) Core.getCore()).getSessionManager().isSessionAlive((Player)o);
	}

	@Override
	public boolean isRegistered(final String nick)
	{
		return ((BukkitLogItCore) Core.getCore()).getAccountManager().isRegistered(nick);
	}

	@Override
	public BukkitLogItPlugin getPlugin()
	{
		System.out.println("getPlugin");
		return (BukkitLogItPlugin)Plugin.getPlugin();
	}

	@Override
	public EventPriority getJoinEventPriority()
	{
		return EventPriority.MONITOR;
	}
}
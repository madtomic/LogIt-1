/*
 * RegisterCommand.java
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
package io.github.lucaseasedup.logit.command;

import static io.github.lucaseasedup.logit.message.MessageHelper.sendMsg;
import static io.github.lucaseasedup.logit.message.MessageHelper.t;
import static io.github.lucaseasedup.logit.util.PlayerUtils.getPlayerIp;
import static io.github.lucaseasedup.logit.util.PlayerUtils.isPlayerOnline;
import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.account.Account;
import io.github.lucaseasedup.logit.common.ReportedException;
import io.github.lucaseasedup.logit.cooldown.LogItCooldowns;
import io.github.lucaseasedup.logit.hooks.AutoInHook;
import io.github.lucaseasedup.logit.storage.Infix;
import io.github.lucaseasedup.logit.storage.SelectorCondition;
import io.github.lucaseasedup.logit.util.PlayerUtils;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RegisterCommand extends LogItCoreObject implements CommandExecutor
{
    @Override
    public boolean onCommand(final CommandSender sender, Command cmd, String label, String[] args)
    {
        final Player player;
        
        if (sender instanceof Player)
        {
            player = (Player) sender;
        }
        else
        {
            player = null;
        }
        
        int minPasswordLength = getConfig("config.yml").getInt("passwords.minLength");
        int maxPasswordLength = getConfig("config.yml").getInt("passwords.maxLength");
        boolean disablePasswords = getConfig("config.yml").getBoolean("passwords.disable");
        boolean codesEnabled = getConfig("config.yml").getBoolean("forceLogin.registerCode.enabled");
        
        if (args.length > 0 && args[0].equals("-x")
                && ((args.length <= 2 && disablePasswords)
                        || (args.length <= 3 && !disablePasswords)))
        {
            if (player != null && (
                   (getCore().isPlayerForcedToLogIn(player)
                           && !getSessionManager().isSessionAlive(player))
                   || !player.hasPermission("logit.register.others")
            ))
            {
                sendMsg(sender, t("noPerms"));
                
                return true;
            }
            
            if (args.length < 2)
            {
                sendMsg(sender, t("paramMissing")
                        .replace("{0}", "player"));
                
                return true;
            }
            
            if (!disablePasswords && args.length < 3)
            {
                sendMsg(sender, t("paramMissing")
                        .replace("{0}", "password"));
                
                return true;
            }
            
            if (getAccountManager().isRegistered(args[1]))
            {
                sendMsg(sender, t("alreadyRegistered.others")
                        .replace("{0}", args[1]));
                
                return true;
            }
            
            if (!disablePasswords && args[2].length() < minPasswordLength)
            {
                sendMsg(sender, t("passwordTooShort")
                        .replace("{0}", String.valueOf(minPasswordLength)));
                
                return true;
            }
            
            if (!disablePasswords && args[2].length() > maxPasswordLength)
            {
                sendMsg(sender, t("passwordTooLong")
                        .replace("{0}", String.valueOf(maxPasswordLength)));
                
                return true;
            }
            
            String password = "";
            
            if (!disablePasswords)
            {
                password = args[2];
            }
            
            try
            {
                ReportedException.incrementRequestCount();
                
                Account account = new Account(args[1]);
                account.changePassword(password);
                
                if (isPlayerOnline(args[1]))
                {
                    Player paramPlayer = PlayerUtils.getPlayer(args[1]);
                    
                    account.setUuid(paramPlayer.getUniqueId());
                    account.setIp(PlayerUtils.getPlayerIp(paramPlayer));
                    account.setDisplayName(paramPlayer.getName());
                }
                
                account.setLastActiveDate(System.currentTimeMillis() / 1000L);
                account.setRegistrationDate(System.currentTimeMillis() / 1000L);
                
                if (!getAccountManager().insertAccount(account).isCancelled())
                {
                    sendMsg(sender, t("createAccount.success.others")
                            .replace("{0}", PlayerUtils.getPlayerRealName(args[1])));
                    
                    if (isPlayerOnline(args[1]))
                    {
                        Player paramPlayer = PlayerUtils.getPlayer(args[1]);
                        
                        if (getSessionManager().getSession(paramPlayer) == null)
                        {
                            getSessionManager().createSession(paramPlayer);
                        }
                        
                        if (!getSessionManager().startSession(paramPlayer).isCancelled())
                        {
                            sendMsg(paramPlayer, t("createAccount.successAndLogin.self"));
                            sendMsg(sender, t("startSession.success.others")
                                    .replace("{0}", paramPlayer.getName()));
                        }
                        else
                        {
                            sendMsg(paramPlayer, t("createAccount.success.self"));
                        }
                        
                        boolean newbieTeleportEnabled = getConfig("config.yml")
                                .getBoolean("newbieTeleport.enabled");
                        
                        if (newbieTeleportEnabled)
                        {
                            Location newbieTeleportLocation = getConfig("config.yml")
                                    .getLocation("newbieTeleport.location")
                                    .toBukkitLocation();
                            
                            paramPlayer.teleport(newbieTeleportLocation);
                        }
                    }
                }
            }
            catch (ReportedException ex)
            {
                sendMsg(sender, t("createAccount.fail.others")
                        .replace("{0}", args[1]));
            }
            finally
            {
                ReportedException.decrementRequestCount();
            }
        }
        else if ((args.length == 0 && disablePasswords) 
        		|| (args.length == 1 && disablePasswords && codesEnabled)
        		|| (args.length <= 2 && !disablePasswords)
        		|| (args.length <= 3 && !disablePasswords && codesEnabled))
        {
            if (player == null)
            {
                sendMsg(sender, t("onlyForPlayers"));
                
                return true;
            }
            
            if (!player.hasPermission("logit.register.self"))
            {
                sendMsg(player, t("noPerms"));
                
                return true;
            }
            
            if (!disablePasswords && args.length < 1)
            {
                sendMsg(player, t("paramMissing")
                        .replace("{0}", "password"));
                
                return true;
            }
            
            if (!disablePasswords && args.length < 2)
            {
                sendMsg(player, t("paramMissing")
                        .replace("{0}", "confirmpassword"));
                
                return true;
            }
            
            if (!disablePasswords && codesEnabled && args.length < 3)
            {
                sendMsg(player, t("paramMissing")
                        .replace("{0}", "code"));
                
                return true;
            }
            
            if(disablePasswords && codesEnabled && args.length < 1)
            {
                sendMsg(player, t("paramMissing")
                        .replace("{0}", "code"));
                
                return true;
            }
            
            if (getCooldownManager().isCooldownActive(player, LogItCooldowns.REGISTER))
            {
                getMessageDispatcher().sendCooldownMessage(player.getName(),
                        getCooldownManager().getCooldownMillis(player, LogItCooldowns.REGISTER));
                
                return true;
            }
            
            boolean takeoverEnabled = getConfig("config.yml")
                    .getBoolean("premiumTakeover.enabled");
            boolean isPremium = AutoInHook.isPremium(player);
            boolean canTakeOver = takeoverEnabled && isPremium;
            boolean isRegistered = getAccountManager().isRegistered(player.getName());
            boolean isTakingOver = canTakeOver && isRegistered;
            
            if (isRegistered && !canTakeOver)
            {
                sendMsg(player, t("alreadyRegistered.self"));
                
                return true;
            }
            
            if (!disablePasswords && args[0].length() < minPasswordLength)
            {
                sendMsg(player, t("passwordTooShort")
                        .replace("{0}", String.valueOf(minPasswordLength)));
                
                return true;
            }
            
            if (!disablePasswords && args[0].length() > maxPasswordLength)
            {
                sendMsg(player, t("passwordTooLong")
                        .replace("{0}", String.valueOf(maxPasswordLength)));
                
                return true;
            }
            
            if (!disablePasswords && !args[0].equals(args[1]))
            {
                sendMsg(player, t("passwordsDoNotMatch"));
                
                return true;
            }
            
            int accountsPerIp = getConfig("config.yml").getInt("accountsPerIp.amount");
            
            if (accountsPerIp >= 0 && !isTakingOver)
            {
                int accountsWithIp = getAccountManager().selectAccounts(
                        Arrays.asList(keys().username(), keys().ip()),
                        new SelectorCondition(keys().ip(), Infix.EQUALS, getPlayerIp(player))
                ).size();
                
                List<String> unrestrictedIps =
                        getConfig("config.yml").getStringList("accountsPerIp.unrestrictedIps");
                
                if (accountsWithIp >= accountsPerIp
                        && !unrestrictedIps.contains(getPlayerIp(player)))
                {
                    sendMsg(player, t("accountsPerIpLimitReached"));
                    
                    return true;
                }
            }
            
            String password = "";
            
            if (!disablePasswords)
            {
                password = args[0];
            }
            
            String code = "";
            
            if (codesEnabled)
            {
            	if(!disablePasswords)
            	{
                    code = args[2];
            	}
            	else
            	{
                    code = args[0];
            	}
            	
            	if(!getRandomCodesManager().getCode(player).equals(code))
            	{
            		sendMsg(player, t("invalidRegisterCode"));
            		return true;
            	}
            	getRandomCodesManager().removeCode(player);
            }
            
            String username = player.getName().toLowerCase();
            
            if (isTakingOver)
            {
                try
                {
                    ReportedException.incrementRequestCount();
                    
                    Account account = getAccountManager().selectAccount(username, Arrays.asList(
                            keys().username()
                    ));
                    
                    account.changePassword(password);
                    account.enqueueSaveCallback(new Account.SaveCallback()
                    {
                        @Override
                        public void onSave(boolean success)
                        {
                            if (success)
                            {
                                sendMsg(sender, t("takeover.success"));
                                
                                if (!getSessionManager().startSession(player).isCancelled())
                                {
                                    sendMsg(sender, t("startSession.success.self"));
                                }
                            }
                            else
                            {
                                sendMsg(sender, t("takeover.fail"));
                            }
                        }
                    });
                }
                catch (ReportedException ex)
                {
                    sendMsg(sender, t("takeover.fail"));
                }
                finally
                {
                    ReportedException.decrementRequestCount();
                }
            }
            else
            {
                try
                {
                    ReportedException.incrementRequestCount();
                    
                    Account account = new Account(username);
                    account.setUuid(player.getUniqueId());
                    account.changePassword(password);
                    account.setIp(PlayerUtils.getPlayerIp(player));
                    account.setLastActiveDate(System.currentTimeMillis() / 1000L);
                    account.setRegistrationDate(System.currentTimeMillis() / 1000L);
                    account.setDisplayName(player.getName());
                    
                    if (!getAccountManager().insertAccount(account).isCancelled())
                    {
                        LogItCooldowns.activate(player, LogItCooldowns.REGISTER);
                        
                        if (!getSessionManager().startSession(player).isCancelled())
                        {
                            sendMsg(sender, t("createAccount.successAndLogin.self"));
                        }
                        else
                        {
                            sendMsg(sender, t("createAccount.success.self"));
                        }
                        
                        boolean newbieTeleportEnabled = getConfig("config.yml")
                                .getBoolean("newbieTeleport.enabled");
                        
                        if (newbieTeleportEnabled)
                        {
                            Location newbieTeleportLocation = getConfig("config.yml")
                                    .getLocation("newbieTeleport.location")
                                    .toBukkitLocation();
                            
                            player.teleport(newbieTeleportLocation);
                        }
                        
                        if (getConfig("config.yml").getBoolean("loginSessions.enabled"))
                        {
                            sendMsg(sender, t("rememberLogin.prompt"));
                        }
                        
                        if (getConfig("config.yml").getBoolean("passwordRecovery.promptToAddEmail")
                                && getConfig("config.yml").getBoolean("passwordRecovery.enabled"))
                        {
                            sendMsg(sender, t("noEmailSet"));
                        }
                    }
                }
                catch (ReportedException ex)
                {
                    sendMsg(sender, t("createAccount.fail.self"));
                }
                finally
                {
                    ReportedException.decrementRequestCount();
                }
            }
        }
        else
        {
            sendMsg(sender, t("incorrectParamCombination"));
        }
        
        return true;
    }
}

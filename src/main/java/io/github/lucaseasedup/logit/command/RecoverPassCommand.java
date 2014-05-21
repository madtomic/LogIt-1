/*
 * RecoverPassCommand.java
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

import static io.github.lucaseasedup.logit.util.MessageHelper._;
import static io.github.lucaseasedup.logit.util.MessageHelper.sendMsg;
import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.ReportedException;
import io.github.lucaseasedup.logit.security.SecurityHelper;
import io.github.lucaseasedup.logit.util.IoUtils;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class RecoverPassCommand extends LogItCoreObject implements CommandExecutor
{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        Player p = null;
        
        if (sender instanceof Player)
        {
            p = (Player) sender;
        }
        
        if (args.length <= 1)
        {
            if (p == null)
            {
                sendMsg(sender, _("onlyForPlayers"));
            }
            else if (!p.hasPermission("logit.recoverpass"))
            {
                sendMsg(p, _("noPerms"));
            }
            else if (args.length < 1)
            {
                sendMsg(p, _("paramMissing").replace("{0}", "email"));
            }
            else if (!getAccountManager().isRegistered(p.getName()))
            {
                sendMsg(p, _("notRegistered.self"));
            }
            else
            {
                try
                {
                    ReportedException.incrementRequestCount();
                    
                    String email = getAccountManager().getEmail(p.getName());
                    
                    if (!args[0].equals(email))
                    {
                        sendMsg(p, _("INCORRECT_EMAIL_ADDRESS"));
                        
                        return true;
                    }
                    
                    String to = email;
                    String from = getConfig().getString("mail.email-address");
                    String subject = getConfig().getString("password-recovery.subject")
                            .replace("%player%", p.getName());
                    
                    int passwordLength = getConfig().getInt("password-recovery.password-length");
                    String newPassword = SecurityHelper.generatePassword(passwordLength,
                            getConfig().getString("password-recovery.password-combination"));
                    getAccountManager().changeAccountPassword(p.getName(), newPassword);
                    
                    File bodyTemplateFile =
                            getDataFile(getConfig().getString("password-recovery.body-template"));
                    String bodyTemplate = IoUtils.toString(bodyTemplateFile);
                    String body = bodyTemplate
                            .replace("%player%", p.getName())
                            .replace("%password%", newPassword);
                    
                    getMailSender().sendMail(Arrays.asList(to), from, subject, body,
                            getConfig().getBoolean("password-recovery.html-enabled"));
                    
                    sendMsg(sender, _("recoverPassword.success.self")
                            .replace("{0}", args[0]));
                    log(Level.FINE, _("recoverPassword.success.log")
                            .replace("{0}", p.getName())
                            .replace("{1}", to));
                }
                catch (ReportedException | IOException ex)
                {
                    sendMsg(sender, _("recoverPassword.fail.self"));
                    log(Level.WARNING, _("recoverPassword.fail.log")
                            .replace("{0}", p.getName()), ex);
                }
                finally
                {
                    ReportedException.decrementRequestCount();
                }
            }
        }
        else
        {
            sendMsg(sender, _("incorrectParamCombination"));
        }
        
        return true;
    }
}

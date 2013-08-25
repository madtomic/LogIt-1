/*
 * SmtpObserver.java
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
package io.github.lucaseasedup.logit.config.observers;

import io.github.lucaseasedup.logit.config.Property;
import io.github.lucaseasedup.logit.config.PropertyObserver;

/**
 * @author LucasEasedUp
 */
public final class SmtpObserver extends PropertyObserver
{
    @Override
    public void update(Property p)
    {
        getMailSender().configure(getConfig().getString("mail.smtp-host"),
                getConfig().getInt("mail.smtp-port"), getConfig().getString("mail.smtp-user"),
                getConfig().getString("mail.smtp-password"));
    }
}

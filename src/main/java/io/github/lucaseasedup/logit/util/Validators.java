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
package io.github.lucaseasedup.logit.util;

import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;
import org.apache.http.conn.util.InetAddressUtils;

public final class Validators
{
	private Validators()
	{
	}

	public static boolean validateEmail(String email)
	{
		if (StringUtils.isBlank(email))
			return false;

		return EMAIL_PATTERN.matcher(email).matches();
	}

	public static boolean validateIp(String ip)
	{
		if (StringUtils.isBlank(ip))
			return false;

		return InetAddressUtils.isIPv4Address(ip)
				|| InetAddressUtils.isIPv6Address(ip);
	}

	public static final Pattern EMAIL_PATTERN = Pattern
			.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
					+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
}

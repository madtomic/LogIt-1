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

import io.github.lucaseasedup.logit.LogItCore;

import java.util.Random;

public final class RandomCodesManager
{
	private RandomCodesManager()
	{
	}

	public static String generateCode()
	{
		StringBuilder sb = new StringBuilder();
		int codeLength = LogItCore.getInstance().getConfig("config.yml")
				.getInt("forceLogin.registerCode.length");
		for (int i = 0; i < codeLength; i++)
		{
			char c = CHARS[RANDOM.nextInt(CHARS.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	private static final Random RANDOM = new Random();
	private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz1234567890"
			.toCharArray();
}

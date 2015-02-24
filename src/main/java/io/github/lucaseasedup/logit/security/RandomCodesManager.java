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

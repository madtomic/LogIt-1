package io.github.lucaseasedup.logit.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.bukkit.entity.Player;

import io.github.lucaseasedup.logit.LogItCoreObject;
import io.github.lucaseasedup.logit.common.Disposable;

public class RandomCodesManager extends LogItCoreObject implements Disposable
{
	public RandomCodesManager()
	{
		codes = new HashMap<Player, String>();
	}
	
	public String addPlayerAndGenerateCode(Player player)
	{
		StringBuilder sb = new StringBuilder();
		int codeLength = getConfig("config.yml").getInt("forceLogin.registerCode.length");
		for (int i = 0; i < codeLength; i++)
		{
		    char c = CHARS[RANDOM.nextInt(CHARS.length)];
		    sb.append(c);
		}
		String code = sb.toString();
		codes.put(player, code);
		return code;
	}
	
	public String getCode(Player player)
	{
		return codes.get(player);
	}
	
	public void removeCode(Player player)
	{
		codes.remove(player);
	}
	
	@Override
	public void dispose()
	{
		if(codes != null)
		{
			Iterator<Player> codesI = codes.keySet().iterator();
			while(codesI.hasNext())
			{
				codes.remove(codesI.next());
			}
			codes = null;
			codesI = null;
		}
	}
	
	private Map<Player, String> codes;
	private static final Random RANDOM = new Random();
	private static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray();
}

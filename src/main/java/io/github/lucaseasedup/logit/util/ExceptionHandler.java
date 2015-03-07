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

import java.util.ArrayList;
import java.util.List;

public final class ExceptionHandler
{	
	private ExceptionHandler()
	{
	}
	
	public static void handleException(Exception e)
	{
		List<String> message = new ArrayList<String>(50);
		
		message.add("BEGIN OF LOGIT ERROR MESSAGE");
		message.add("A fatal exception thrown in LogIt");
		message.add(" ");
		message.add("LogIt Version: "+logitVersion);
		message.add("Core enabled: "+ (isCoreEnabled == true ? "yes" : "no"));
		message.add("Server version: "+serverVersion);
		message.add(" ");
		message.add(" ");
		message.add("Exception info");
		message.add(" ");
		message.add(e.getClass().getSimpleName());
		message.add("Message: "+e.getMessage());
		message.add("Stack trace: ");
		for(StackTraceElement element : e.getStackTrace())
		{
			StringBuilder sb = new StringBuilder();
			sb.append(" ");
			sb.append(element.getClassName());
			sb.append("/");
			sb.append(element.getMethodName());
			sb.append(" (");
			sb.append(element.getLineNumber());
			sb.append(")");
			message.add(sb.toString());
			sb = null;
		}
		message.add(" ");
		message.add("END OF LOGIT ERROR MESSAGE");
		
		for(String s : message)
		{
			System.out.println(s);
		}
	}
	
	public static String logitVersion = "";
	public static boolean isCoreEnabled = false;
	public static String serverVersion = "";
}

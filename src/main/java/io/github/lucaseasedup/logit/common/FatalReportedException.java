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
package io.github.lucaseasedup.logit.common;

/**
 * Exception that does not need any further logging,
 * and is a signal to stop execution immediately.
 */
public class FatalReportedException extends Exception
{
	private FatalReportedException()
	{
	}

	private FatalReportedException(String msg)
	{
		super(msg);
	}

	private FatalReportedException(Throwable cause)
	{
		super(cause);
	}

	private FatalReportedException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public void rethrow() throws FatalReportedException
	{
		throw this;
	}

	public static void throwNew() throws FatalReportedException
	{
		throw new FatalReportedException();
	}

	public static void throwNew(String msg) throws FatalReportedException
	{
		throw new FatalReportedException(msg);
	}

	public static void throwNew(Throwable cause) throws FatalReportedException
	{
		throw new FatalReportedException(cause);
	}

	public static void throwNew(String msg, Throwable cause)
			throws FatalReportedException
	{
		throw new FatalReportedException(msg, cause);
	}

	private static final long serialVersionUID = 1L;
}

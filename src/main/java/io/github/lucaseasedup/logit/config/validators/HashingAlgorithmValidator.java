/*
 * HashingAlgorithmValidator.java
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
package io.github.lucaseasedup.logit.config.validators;

import io.github.lucaseasedup.logit.config.PropertyType;
import io.github.lucaseasedup.logit.config.PropertyValidator;
import io.github.lucaseasedup.logit.security.HashingAlgorithm;

public final class HashingAlgorithmValidator implements PropertyValidator
{
	@Override
	public boolean validate(String path, PropertyType type, Object value)
	{
		if (value == null)
			return false;

		HashingAlgorithm algorithmType = HashingAlgorithm.decode(value
				.toString());

		return algorithmType != null
				&& algorithmType != HashingAlgorithm.AUTHME;
	}
}

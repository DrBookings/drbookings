package com.github.drbookings;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Scripting {

	private static final Logger logger = LoggerFactory.getLogger(Scripting.class);

	public static Number evaluateExpression(final String expression) {
		if (expression != null && expression.trim().length() > 0) {
			try {
				final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
				final Object result = engine.eval(expression);
				// if (logger.isDebugEnabled()) {
				// logger.debug("Expression result: " + result);
				// }
				if (result instanceof Number) {
					return ((Number) result);
				}
			} catch (final ScriptException e) {
				logger.debug(e.toString());
			}
		}
		return 0;
	}
}

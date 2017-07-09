package com.github.drbookings;

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

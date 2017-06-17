package com.github.drbookings.model.data;

public class MatchException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 3903873672139845L;

	public MatchException() {

	}

	public MatchException(final String message) {
		super(message);

	}

	public MatchException(final Throwable cause) {
		super(cause);

	}

	public MatchException(final String message, final Throwable cause) {
		super(message, cause);

	}

	public MatchException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

}

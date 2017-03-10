package com.github.drbookings;

public class OverbookingException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = 4308273152755034370L;

    public OverbookingException() {

    }

    public OverbookingException(final String message) {
	super(message);

    }

    public OverbookingException(final String message, final Throwable cause) {
	super(message, cause);

    }

    public OverbookingException(final String message, final Throwable cause, final boolean enableSuppression,
	    final boolean writableStackTrace) {
	super(message, cause, enableSuppression, writableStackTrace);

    }

    public OverbookingException(final Throwable cause) {
	super(cause);

    }

}

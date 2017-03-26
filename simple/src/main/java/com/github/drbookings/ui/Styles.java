package com.github.drbookings.ui;

public class Styles {

    public static String getBackgroundStyleSource() {
	return getBackgroundStyleSource("");
    }

    public static String getBackgroundStyleSource(final String id) {
	if ("airbnb".equalsIgnoreCase(id)) {
	    return "source-background-airbnb";
	} else if ("booking".equalsIgnoreCase(id)) {
	    return "source-background-booking";
	}
	return "source-background-other";
    }

}

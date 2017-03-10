package com.github.drbookings.ui;

public class Styles {

    public static String getBackgroundStyleBookingSource() {
	return getBackgroundStyleBookingSource("");
    }

    public static String getBackgroundStyleBookingSource(final String id) {
	if ("airbnb".equalsIgnoreCase(id)) {
	    return "-fx-background-color: #FF8989;";
	} else if ("booking".equalsIgnoreCase(id)) {
	    return "-fx-background-color: #86D9FF;";
	}
	return "-fx-background-color: rgba(215,215,215);";
    }

}

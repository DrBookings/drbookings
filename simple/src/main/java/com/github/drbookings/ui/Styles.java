package com.github.drbookings.ui;

public class Styles {

    public static String getBackgroundStyleBookingSource() {
	return getBackgroundStyleBookingSource("");
    }

    public static String getBackgroundStyleBookingSource(final String id) {
	if ("airbnb".equalsIgnoreCase(id)) {
	    return "-fx-background-color: rgba(255,90,95,0.3);";
	} else if ("booking".equalsIgnoreCase(id)) {
	    return "-fx-background-color: rgba(8,152,255,0.3);";
	}
	return "-fx-background-color: rgba(215,215,215);";
    }

}

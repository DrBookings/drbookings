package com.github.drbookings.ui.io.xls.booking.runnables;

import java.time.format.DateTimeFormatter;

public class FileFormatBookingXLS {

	public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public static final String IDENTIFIER_BOOKING_CLIENT_NAMES = "Guest name(s)";
	public static final String IDENTIFIER_BOOKING_NUMBER = "Book number";

	public static final String IDENTIFIER_CHECK_IN = "Check-in";

	public static final String IDENTIFIER_CHECK_OUT = "Check-out";

}

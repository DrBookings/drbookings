package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.stream.Stream;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.BookingEntry;

public class Bookings {

    public static Stream<BookingEntry> getBookingEntriesToday(final MainManager manager) {
	return getBookingEntries(manager, LocalDate.now());
    }

    public static Stream<BookingEntry> getBookingEntries(final MainManager manager, final LocalDate date) {
	return manager.getBookingEntries().stream().filter(b -> b.getDate().equals(date));
    }

}

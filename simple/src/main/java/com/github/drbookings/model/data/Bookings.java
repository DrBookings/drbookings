package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.drbookings.DateRange;
import com.github.drbookings.ui.BookingEntry;

public class Bookings {

	public static long countNights(final Collection<? extends Booking> bookings) {
		return bookings.stream().mapToLong(b -> b.getNumberOfNights()).sum();
	}

	public static double getServiceFeePercentAmount(final Booking booking) {
		return (booking.getGrossEarnings() - booking.getCleaningFees()) * booking.getServiceFeesPercent() / 100.0;
	}

	public static Collection<BookingEntry> toEntries(final Collection<Booking> bookings) {
		final Collection<BookingEntry> result = new ArrayList<>();
		for (final Booking b : bookings) {
			for (final LocalDate d : new DateRange(b.getCheckIn(), b.getCheckOut())) {
				result.add(new BookingEntry(d, b));
			}
		}
		return result;
	}

	public static Collection<BookingEntry> toEntries(final Booking... bookings) {
		return toEntries(Arrays.asList(bookings));
	}
}

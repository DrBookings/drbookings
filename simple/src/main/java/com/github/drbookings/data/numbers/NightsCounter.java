package com.github.drbookings.data.numbers;

import java.util.Collection;
import java.util.stream.Stream;

import com.github.drbookings.model.BookingEntry;

/**
 *
 * @author Alexander Kerner
 *
 */
public class NightsCounter {

    public static long countNights(final Collection<? extends BookingEntry> bookings) {
	return countNights(bookings.stream());

    }

    public static long countNights(final Stream<? extends BookingEntry> stream) {
	return stream.filter(b -> !b.isCheckOut()).count();
    }

}

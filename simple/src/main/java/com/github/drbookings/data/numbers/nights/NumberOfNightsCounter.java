package com.github.drbookings.data.numbers.nights;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

import com.github.drbookings.LocalDates;
import com.github.drbookings.model.data.BookingBean;
import com.google.common.collect.Range;

public class NumberOfNightsCounter {

    public static long countNights(final Collection<? extends BookingBean> bookings) {
	long result = 0;
	for (final BookingBean bb : bookings) {
	    result += bb.getNumberOfNights();
	}
	return result;
    }

    public static long countNights(final Collection<? extends BookingBean> bookings, final YearMonth month) {
	return countNights(bookings, LocalDates.toDateRange(month));
    }

    public static long countNights(final Collection<? extends BookingBean> bookings, final Range<LocalDate> dateRange) {

	long result = 0;

	for (final BookingBean bb : bookings) {

	    if (dateRange.isConnected(bb.getDateRange())) {
		final Range<LocalDate> intersectionRange = dateRange.intersection(bb.getDateRange());

		LocalDate lower = intersectionRange.lowerEndpoint();
		final LocalDate upper = intersectionRange.upperEndpoint();

		if (bb.getCheckIn().isBefore(lower)) {
		    // if lower is not a check-in, then its a stay-over, so this night needs to be
		    // counted as well.
		    lower = lower.minusDays(1);
		}

		result += LocalDates.getNumberOfNights(lower, upper);
	    }
	}

	return result;
    }

}

package com.github.drbookings.data.numbers.nights;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Stream;

import com.github.drbookings.data.numbers.Numbers;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.ui.BookingsByOrigin;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-23
 *
 */

public class NightsPercentProvider {

    private final BookingsByOrigin<BookingBean> bookings;

    public NightsPercentProvider(final BookingsByOrigin<BookingBean> bookings) {
	this.bookings = bookings;
    }

    static long countNights(final BookingOrigin origin, final Stream<BookingBean> bookings) {
	return bookings.filter(b -> origin == null || origin.equals(b.getBookingOrigin()))
		.mapToLong(BookingBean::getNumberOfNights).sum();
    }

    static long countNights(final BookingOrigin origin, final Collection<BookingBean> bookings) {
	return countNights(origin, bookings.stream());
    }

    public BigDecimal getPercent(final BookingOrigin origin, final boolean cheat) {
	final BigDecimal totalNightsCount = BigDecimal
		.valueOf(countNights(null, bookings.getAllBookings(cheat).stream()));
	final BigDecimal thisOriginCount = BigDecimal
		.valueOf(countNights(origin, bookings.getAllBookings(cheat).stream()));
	final BigDecimal result = thisOriginCount.divide(totalNightsCount, Numbers.DEFAULT_SCALE,
		Numbers.DEFAULT_ROUNDING_MODE);
	return result;
    }

    public BigDecimal getPercent(final String origin, final boolean cheat) {

	return getPercent(new BookingOrigin(origin), cheat);
    }

}

package com.github.drbookings.data.numbers.nights;

import java.math.BigDecimal;
import java.util.stream.Stream;

import com.github.drbookings.data.numbers.NumberSupplier;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.ui.BookingsByOrigin;

public class NightsPercentProvider {

    private final BookingsByOrigin<BookingBean> bookings;

    public NightsPercentProvider(final BookingsByOrigin<BookingBean> bookings) {
	this.bookings = bookings;
    }

    private static long countNights(final BookingOrigin origin, final Stream<BookingBean> bookings) {
	return bookings.filter(b -> origin == null || origin.equals(b.getBookingOrigin()))
		.mapToLong(BookingBean::getNumberOfNights).sum();
    }

    public BigDecimal getPercent(final BookingOrigin origin, final boolean cheat) {
	final BigDecimal totalNightsCount = BigDecimal
		.valueOf(countNights(null, bookings.getAllBookings(cheat).stream()));
	final BigDecimal thisOriginCount = BigDecimal
		.valueOf(countNights(origin, bookings.getAllBookings(cheat).stream()));
	return thisOriginCount.divide(totalNightsCount, NumberSupplier.DEFAULT_SCALE,
		NumberSupplier.DEFAULT_ROUNDING_MODE);
    }

    public BigDecimal getPercent(final String origin, final boolean cheat) {

	return getPercent(new BookingOrigin("airbnb"), cheat);
    }

}

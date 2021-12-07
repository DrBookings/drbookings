/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.data.numbers.nights;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Stream;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingOrigin;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.Numbers;

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

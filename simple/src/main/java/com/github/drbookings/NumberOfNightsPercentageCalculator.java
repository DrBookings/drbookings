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

package com.github.drbookings;

import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

public class NumberOfNightsPercentageCalculator extends AbstractNumberOfNightsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(NumberOfNightsPercentageCalculator.class);

    private static double getPercentageThisBookings(final long thisBookingsCount, final long allBookingsCount) {

	return (double) thisBookingsCount / allBookingsCount;
    }

    public NumberOfNightsPercentageCalculator(final Collection<? extends BookingBean> bookings) {

	super(bookings);
	setIgnorePaymentDate(true);
    }

    public NumberOfNightsPercentageCalculator(final Range<LocalDate> dates,
	    final Collection<? extends BookingBean> bookings) {

	super(dates, bookings);
	setIgnorePaymentDate(true);
    }

    public NumberOfNightsPercentageCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings) {

	super(month, bookings);
	setIgnorePaymentDate(true);
    }

    public double getPercentageForOrigin(final BookingOrigin origin, final boolean cheat) {

	// cheat is true, but provided origin itself is cheat
	if (cheat && ByOriginFilter.CHEAT_FILTER_BOOKING_ORIGIN.test(origin))
	    return 0d;
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(getBookings());
	final long allBookings = new NumberOfNightsCounter(getDateRange(), bbo.getAllBookings(cheat))
		.setIgnorePaymentDate(isIgnorePaymentDate()).countNights();
	final long thisBookings = new NumberOfNightsCounter(getDateRange(), bbo.getByOrigin(origin))
		.setIgnorePaymentDate(isIgnorePaymentDate()).countNights();
	final double result = getPercentageThisBookings(thisBookings, allBookings);
	if (logger.isDebugEnabled()) {
	    logger.debug("Percent bookings for " + origin + ": " + String.format("%6.4f", result));
	}
	return result;
    }

    public double getPercentageForOrigin(final String origin, final boolean cheat) {

	return getPercentageForOrigin(new BookingOrigin(origin), cheat);
    }

    @Override
    public NumberOfNightsPercentageCalculator setIgnorePaymentDate(final boolean ignorePaymentDate) {

	super.setIgnorePaymentDate(ignorePaymentDate);
	return this;
    }
}

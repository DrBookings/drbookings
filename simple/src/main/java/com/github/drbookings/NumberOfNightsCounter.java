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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;

public class NumberOfNightsCounter extends AbstractNumberOfNightsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(NumberOfNightsCounter.class);

    public NumberOfNightsCounter(final Collection<? extends BookingBean> bookings) {

	super(bookings);
    }

    public NumberOfNightsCounter(final YearMonth month, final BookingBean... bookings) {

	super(month, Arrays.asList(bookings));
    }

    public NumberOfNightsCounter(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings) {

	super(dates, bookings);
    }

    public NumberOfNightsCounter(final YearMonth month, final Collection<? extends BookingBean> bookings) {

	super(month, bookings);
    }

    private long bla(final BookingBean bb) {

	if (getDateRange().isConnected(bb.getDateRange())) {
	    final Range<LocalDate> intersectionRange = getDateRange().intersection(bb.getDateRange());
	    final LocalDate lower = intersectionRange.lowerEndpoint();
	    LocalDate upper = intersectionRange.upperEndpoint();
	    if (bb.getCheckOut().isAfter(upper)) {
		// if upper is not the check-out, then its a stay-over, so this night needs to
		// be counted as well. Night belongs to day before, not to day after.
		upper = upper.plusDays(1);
	    }

	    final long nights = LocalDates.getNumberOfNights(lower, upper);
	    return nights;
	}
	return 0;
    }

    public long countNights() {

	long result = 0;
	for (final BookingBean bb : getBookings()) {
	    if (getDateRange() == null) {
		result += bb.getNumberOfNights();
	    } else {
		if (isIgnorePaymentDate() || bookingInRange(bb)) {
		    result += bla(bb);
		} else {
		    if (logger.isDebugEnabled()) {
			logger.debug("Skipping booking " + bb);
		    }
		}
	    }
	}
	return result;
    }

    @Override
    public NumberOfNightsCounter setIgnorePaymentDate(final boolean ignorePaymentDate) {

	super.setIgnorePaymentDate(ignorePaymentDate);
	return this;
    }
}

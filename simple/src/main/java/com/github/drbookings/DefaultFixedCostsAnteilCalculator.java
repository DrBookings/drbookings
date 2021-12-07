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

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

/**
 * Calculator to calculate the fixed costs for a specific origin. Note that
 * *all* nights are considered, not only in-payment-range bookings.
 *
 *
 *
 * @author Alexander Kerner
 * @date 2018-08-04
 *
 */
public class DefaultFixedCostsAnteilCalculator extends BookingsDateRangeHandler {

    public DefaultFixedCostsAnteilCalculator(final Range<LocalDate> dates,
	    final Collection<? extends BookingBean> bookings) {

	super(dates, bookings);
    }

    public DefaultFixedCostsAnteilCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings) {

	super(month, bookings);
    }

    public MonetaryAmount getFixkostenAnteil(final String origin, final boolean cheat,
	    final MonetaryAmount additionalCostsAllRooms) {

	return getFixkostenAnteil(new BookingOrigin(origin), cheat, additionalCostsAllRooms);
    }

    public MonetaryAmount getFixkostenAnteil(final BookingOrigin origin, final boolean cheat,
	    final MonetaryAmount additionalCostsAllRooms) {

	// the percentage of nights for this origin in the provided date range. Note
	// that *all* nights are considered, not only bookings in payment range.
	final double percentageThisBookings = new NumberOfNightsPercentageCalculator(getDateRange(), getBookings())
		.setIgnorePaymentDate(true).getPercentageForOrigin(origin, cheat);
	final MonetaryAmount result = additionalCostsAllRooms.multiply(percentageThisBookings);
	return result;
    }
}

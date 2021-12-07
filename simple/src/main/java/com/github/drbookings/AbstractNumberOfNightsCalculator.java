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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

public class AbstractNumberOfNightsCalculator extends BookingsPaymentDateRangeHandler {

    public static final boolean DEFAULT_IGNORE_PAYMENT_DATE = true;

    private boolean ignorePaymentDate = DEFAULT_IGNORE_PAYMENT_DATE;

    public AbstractNumberOfNightsCalculator(final Collection<? extends BookingBean> bookings) {
	super(bookings);

    }

    public AbstractNumberOfNightsCalculator(final Range<LocalDate> dates,
	    final Collection<? extends BookingBean> bookings) {
	super(dates, bookings);

    }

    public AbstractNumberOfNightsCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings) {
	super(month, bookings);

    }

    public boolean isIgnorePaymentDate() {
	return ignorePaymentDate;
    }

    public AbstractNumberOfNightsCalculator setIgnorePaymentDate(final boolean ignorePaymentDate) {
	this.ignorePaymentDate = ignorePaymentDate;
	return this;
    }

}

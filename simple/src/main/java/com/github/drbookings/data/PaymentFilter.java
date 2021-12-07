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

package com.github.drbookings.data;

import com.github.drbookings.BookingEntry;

import java.time.YearMonth;
import java.util.function.Predicate;

/**
 * A {@link Predicate} to filter for bookings that should be payed.
 *
 * @author Alexander Kerner
 *
 */
@Deprecated
public abstract class PaymentFilter implements Predicate<BookingEntry> {

    private final YearMonth month;

    /**
     * Creates a new filter that filters by given payment month. If {@code null},
     * entries will be filtered by {@link BookingEntry#isPaymentDone()}.
     *
     * @param month
     *            The payment month
     */
    public PaymentFilter(final YearMonth month) {

	this.month = month;
    }

    /**
     * Creates a new filter that filters by {@link BookingEntry#isPaymentDone()}.
     */
    public PaymentFilter() {

	this(null);
    }

    protected YearMonth getMonth() {
	return month;
    }

}

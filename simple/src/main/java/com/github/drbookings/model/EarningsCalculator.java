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

package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;

import com.github.drbookings.model.data.BookingBean;
import com.google.common.collect.Range;

public class EarningsCalculator {

    public static final boolean DEFAULT_PAYMENT_DONE = true;

    public static final boolean DEFAULT_NET_EARNINGS = true;

    private Range<LocalDate> dateRange;
    private boolean paymentDone = DEFAULT_PAYMENT_DONE;

    private boolean netEarnings = DEFAULT_NET_EARNINGS;

    public float calculateEarnings(final BookingBean... bookings) {
	return calculateEarnings(Arrays.asList(bookings));
    }

    public float calculateEarnings(final Collection<? extends BookingBean> bookings) {
	return 0;
    }

    public EarningsCalculator filterForNetEarnings(final boolean netEarnigns) {
	this.netEarnings = netEarnigns;
	return this;
    }

    public EarningsCalculator filterForPaymentDone(final boolean paymentDone) {
	this.paymentDone = paymentDone;
	return this;
    }

    public EarningsCalculator filterToDateRange(final Range<LocalDate> dateRange) {
	this.dateRange = dateRange;
	return this;
    }

    public Range<LocalDate> getDateRange() {
	return dateRange;
    }

    public boolean isNetEarnings() {
	return netEarnings;
    }

    public boolean isPaymentDone() {
	return paymentDone;
    }

}

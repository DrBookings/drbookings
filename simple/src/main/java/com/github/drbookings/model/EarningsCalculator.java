/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.ui.BookingEntry;

import java.util.Collection;
import java.util.stream.Stream;

public class EarningsCalculator {

    public static final boolean DEFAULT_PAYMENT_DONE = true;

    public static final boolean DEFAULT_NET_EARNINGS = true;

    private boolean paymentDone = DEFAULT_PAYMENT_DONE;

    private boolean netEarnings = DEFAULT_NET_EARNINGS;

    public boolean isPaymentDone() {
        return paymentDone;
    }

    public boolean isNetEarnings() {
        return netEarnings;
    }

    public EarningsCalculator filterForNetEarnings(boolean netEarnigns) {
        this.netEarnings = netEarnigns;
        return this;
    }

    public EarningsCalculator filterForPaymentDone(boolean paymentDone) {
        this.paymentDone = paymentDone;
        return this;
    }

    public float calculateEarnings(Collection<? extends BookingEntry> bookings) {
        Stream<? extends BookingEntry> bookingStream = bookings.stream();
        if (paymentDone) {
            bookingStream = bookings.stream().filter(b -> b.isPaymentDone());
        }
        return (float) bookingStream.mapToDouble(b -> b.getEarnings(isNetEarnings())).sum();
    }
}

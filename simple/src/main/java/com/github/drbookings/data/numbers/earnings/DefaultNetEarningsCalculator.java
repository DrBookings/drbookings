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

package com.github.drbookings.data.numbers.earnings;

import java.math.BigDecimal;

import com.github.drbookings.data.numbers.DefaultServiceFeesCalculator;
import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.data.BookingBean;

public class DefaultNetEarningsCalculator implements NetEarningsCalculator {

    public DefaultNetEarningsCalculator() {

    }

    @Override
    public BigDecimal apply(final BookingBean booking) {
	final BigDecimal gross = BigDecimal.valueOf(booking.getGrossEarnings());
	final BigDecimal serviceFees = new DefaultServiceFeesCalculator().apply(booking);
	return gross.subtract(serviceFees);
    }

    /**
     * Returns the earnings for this booking entry. That is, the booking earnings
     * per night. All earnings from all booking entries from the same booking are
     * always the same.
     *
     * @param booking
     *            the {@link BookingEntry} to calculate net earnings for
     * @return net earnings for given booking entry
     */
    @Override
    public BigDecimal apply(final BookingEntry booking) {
	return NetEarningsCalculator.super.apply(booking);
    }

}

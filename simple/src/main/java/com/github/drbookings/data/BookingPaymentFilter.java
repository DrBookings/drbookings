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

/**
 * Booking.com pays bookings once per month by check-out date.
 *
 * @author Alexander Kerner
 *
 */
public class BookingPaymentFilter extends PaymentFilter {

    public BookingPaymentFilter() {
	super();

    }

    public BookingPaymentFilter(final YearMonth month) {
	super(month);

    }

    @Override
    public boolean test(final BookingEntry booking) {
	if (getMonth() == null) {
	    return new SimplePaymentFilter().test(booking);
	}
	return YearMonth.from(booking.getElement().getCheckOut()).equals(getMonth());
    }

}

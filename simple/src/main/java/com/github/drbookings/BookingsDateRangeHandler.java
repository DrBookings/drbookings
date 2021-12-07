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

/**
 *
 * @author Alexander Kerner
 * @date 2018-07-16
 *
 */
public class BookingsDateRangeHandler extends SimpleDateRangeHandler {

    private final Collection<? extends BookingBean> bookings;

    public BookingsDateRangeHandler(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings) {
	super(dates);
	this.bookings = bookings;
    }

    public BookingsDateRangeHandler(final YearMonth month, final Collection<? extends BookingBean> bookings) {
	super(month);
	this.bookings = bookings;
    }

    public Collection<? extends BookingBean> getBookings() {
	return bookings;
    }

}

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

package com.github.drbookings.data.numbers;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingsPaymentDateRangeHandler;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * 
 * @author Alexander Kerner
 * @date 2018-08-04
 *
 */
public class BookingsByPaymentDateFilter extends BookingsPaymentDateRangeHandler implements Predicate<BookingBean> {

    public BookingsByPaymentDateFilter(Collection<? extends BookingBean> bookings) {
	super(bookings);
    }

    public BookingsByPaymentDateFilter(Range<LocalDate> dates, Collection<? extends BookingBean> bookings) {
	super(dates, bookings);
    }

    public BookingsByPaymentDateFilter(YearMonth month, Collection<? extends BookingBean> bookings) {
	super(month, bookings);
    }

    public BookingsByPaymentDateFilter(Range<LocalDate> dates) {
	super(dates, null);
    }

    public BookingsByPaymentDateFilter(YearMonth month) {
	super(month, null);
    }

    @Override
    public boolean test(BookingBean t) {
	if (getDateRange() == null) {
	    return true;
	} else {
        return bookingInRange(t);
	}
    }

    public long count() {
	return getBookings().stream().filter(this).count();

    }

    public Collection<BookingBean> getFiltered() {
	return getBookings().stream().filter(this).collect(Collectors.toList());
    }

}

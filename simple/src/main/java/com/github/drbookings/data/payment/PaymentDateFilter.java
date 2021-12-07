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

package com.github.drbookings.data.payment;

import com.github.drbookings.BookingBean;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated
public class PaymentDateFilter {

    private final Range<LocalDate> dates;

    public static final boolean DEFAULT_CHECK_IS_PAYED_FLAG = true;

    private boolean checkIsPayedFlag = DEFAULT_CHECK_IS_PAYED_FLAG;

    public boolean isCheckIsPayedFlag() {
	return checkIsPayedFlag;
    }

    public PaymentDateFilter setCheckIsPayedFlag(final boolean isPayed) {
	this.checkIsPayedFlag = isPayed;
	return this;
    }

    public PaymentDateFilter(final Range<LocalDate> dates) {
	this.dates = dates;
    }

    public PaymentDateFilter(final YearMonth of) {
	this.dates = Range.closed(of.atDay(01), of.atEndOfMonth());
    }

    public List<BookingBean> filter(final Collection<? extends BookingBean> bookings) {
	final List<BookingBean> result = prefilter(bookings);
	for (final BookingBean b : bookings) {

	}
	return result;
    }

    List<BookingBean> prefilter(final Collection<? extends BookingBean> bookings) {
	List<BookingBean> result;
	if (isCheckIsPayedFlag()) {
	    result = filterPayedFlag(bookings);
	}
	result = bookings.stream().filter(b -> b.getCheckOut().plusDays(1).isBefore(dates.upperEndpoint())
		&& b.getCheckIn().minusDays(1).isBefore(dates.lowerEndpoint())).collect(Collectors.toList());
	return result;
    }

    public static List<BookingBean> filterPayedFlag(final Collection<? extends BookingBean> bookings) {
	return bookings.stream().filter(BookingBean::isPaymentDone).collect(Collectors.toList());
    }

}

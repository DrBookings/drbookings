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
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 *
 * @author Alexander Kerner
 * @date 2018-10-27
 *
 */
public class PaymentDateFilter4 implements Predicate<BookingBean> {

    private final static PayoutDateProvider dateProvider = new PayoutDateProvider();
    private final Range<LocalDate> dateRange;

    public PaymentDateFilter4() {

	dateRange = null;
    }

    public PaymentDateFilter4(final Range<LocalDate> dates) {

	dateRange = dates;
    }

    public PaymentDateFilter4(final YearMonth month) {

	dateRange = LocalDates.toDateRange(month);
    }

    public boolean bookingInRange(final BookingBean b) {

	final List<Payment> payments = b.getPayments();
	if ((payments == null) || payments.isEmpty())
	    return (getDateRange() == null) || getDateRange().contains(getPaymentDate(b));
	final List<LocalDate> paymentDates = payments.stream().map(p -> p.getDate()).collect(Collectors.toList());
	return (getDateRange() == null) || getDateRange().isConnected(LocalDates.toDateRange(paymentDates));
    }

    public Range<LocalDate> getDateRange() {

	return dateRange;
    }

    public LocalDate getPaymentDate(final BookingBean b) {

	final LocalDate result = dateProvider.getPayoutDate(b);
	return result;
    }

    @Override
    public boolean test(final BookingBean t) {

	return bookingInRange(t);
    }
}

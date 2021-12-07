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
import java.util.function.Predicate;

/**
 * @deprecated use {@link PaymentsDoneFilter}
 * @author Alexander Kerner
 * @date 2018-06-24
 *
 */
@Deprecated
public class PaymentDateFilter2 implements Predicate<BookingEntry> {

    private final Range<LocalDate> dates;

    public PaymentDateFilter2(final Range<LocalDate> dates) {
	this.dates = dates;
    }

    public PaymentDateFilter2(final YearMonth of) {
	this.dates = Range.closed(of.atDay(01), of.atEndOfMonth());
    }

    @Override
    public boolean test(final BookingEntry be) {
	final BookingBean t = be.getElement();
	return t.isSplitBooking()
		|| t.getDateOfPayment() != null && t.getDateOfPayment().isAfter(dates.lowerEndpoint().minusDays(1))
			&& t.getDateOfPayment().isBefore(dates.upperEndpoint().plusDays(1));
    }

}

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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.Payment;
import com.github.drbookings.model.Payments;
import com.github.drbookings.model.data.BookingBean;
import com.google.common.collect.Range;

public class PaymentDoneFilter {

    private final Range<LocalDate> dates;

    public PaymentDoneFilter(final Range<LocalDate> dates) {
	this.dates = dates;
    }

    public PaymentDoneFilter(final YearMonth of) {
	this.dates = Range.closed(of.atDay(01), of.atEndOfMonth());
    }

    public boolean test(final BookingEntry b) {
	return test(b.getElement());
    }

    public boolean test(final BookingBean b) {
	final List<Payment> payedInRange = Payments.getPaymentInRange(dates, b.getPayments());
	return b.isPaymentDone() && !payedInRange.isEmpty();
    }

}

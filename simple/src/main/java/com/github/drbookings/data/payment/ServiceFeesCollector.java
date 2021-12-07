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

import com.github.drbookings.*;
import com.google.common.collect.Range;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceFeesCollector extends BookingsPaymentDateRangeHandler {

    public ServiceFeesCollector(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings) {
	super(dates, bookings);

    }

    public ServiceFeesCollector(final YearMonth month, final Collection<? extends BookingBean> bookings) {
	super(month, bookings);

    }

    public List<Payment> collect() {

	final DefaultServiceFeesSupplier calculator = new DefaultServiceFeesSupplier();
	final List<Payment> result = new ArrayList<>();
	for (final BookingBean b : getBookings()) {
	    if (bookingInRange(b)) {
		final MonetaryAmount serviceFees = calculator.apply(b);
		final Payment p = new PaymentImpl(getPaymentDate(b), serviceFees);
		result.add(p);
	    }
	}
	return result;
    }

}

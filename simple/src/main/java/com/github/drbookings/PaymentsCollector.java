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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PaymentsCollector extends SimpleDateRangeHandler {

	private static final Logger logger = LoggerFactory.getLogger(PaymentsCollector.class);

	public PaymentsCollector(final Range<LocalDate> dates) {

		super(dates);
	}

	public PaymentsCollector(final YearMonth of) {

		super(of);
	}

	public List<Payment> collect(final Collection<? extends PaymentProvider> elements) {

		final List<Payment> result = new ArrayList<>();
		for(final PaymentProvider bb : elements) {
			if(bb.getPayments().isEmpty()) {
				if(bb instanceof BookingBean) {
					final BookingBean bbb = (BookingBean)bb;
					final float payment = bbb.getGrossEarnings();
					if(bbb.isPaymentDone() && (payment != 0)) {
						final LocalDate date = new PayoutDateProvider().getPayoutDate(bbb);
						if(getDateRange().contains(date)) {
							// System.err.println(payment);
							final Payment p = new PaymentImpl(date, Float.toString(payment));
							if(logger.isInfoEnabled()) {
								logger.info("No payments found for " + bbb + ", creating one from gross earnings (" + p + ")");
							}
							result.add(p);
						}
					}
				}
			}
			for(final Payment p : bb.getPayments()) {
				if(getDateRange().contains(p.getDate())) {
					result.add(p);
				}
			}
		}
		return result;
	}
}

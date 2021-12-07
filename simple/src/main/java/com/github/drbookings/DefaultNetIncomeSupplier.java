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

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Calculates the net income. That is, the gross income minus cleaning fees and
 * service fees.
 *
 * @see DefaultNetEarningsSupplier
 *
 * @author Alexander Kerner
 * @date 2018-09-18
 *
 */
public class DefaultNetIncomeSupplier extends AbstractMonetaryAmountFromBookingsSupplier implements NetIncomeSupplier {

	public DefaultNetIncomeSupplier(final Range<LocalDate> dates) {

		super(dates);
	}

	public DefaultNetIncomeSupplier(final YearMonth month) {

		super(month);
	}

	public DefaultNetIncomeSupplier() {

		super();
	}

	@Override
	public MonetaryAmount applyInDateRange(final BookingBean booking) {

		final MonetaryAmount gross = new DefaultGrossPaymentsSupplier(getDateRange()).apply(booking);
		final MonetaryAmount serviceFees = new DefaultServiceFeesSupplier(getDateRange()).apply(booking);
		final MonetaryAmount cleaningFees = new DefaultCleaningFeesSupplier(getDateRange()).apply(booking);
		final MonetaryAmount result = gross.subtract(serviceFees).subtract(cleaningFees);
		return result;
	}
}

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
import java.util.Collection;

/**
 *
 * Calculates the turnover. That is, the gross income minus cleaning fees. The
 * turnover is also called "Commissionable Amount".
 *
 * @see DefaultGrossPaymentsSupplier
 *
 * @author Alexander Kerner
 * @date 2018-07-22
 *
 */
public class DefaultTurnoverCalculator extends AbstractMonetaryAmountFromBookingsSupplier {

	public DefaultTurnoverCalculator(final Range<LocalDate> dates) {

		super(dates);
	}

	public DefaultTurnoverCalculator(final YearMonth month) {

		super(month);
	}

	public DefaultTurnoverCalculator() {

		super();
	}

	/**
	 * Returns the turnover. That is, gross income minus cleaning fees.
	 */
	@Override
	public MonetaryAmount apply(final Collection<? extends BookingBean> bookings) {

		return super.apply(bookings);
	}

	@Override
	protected MonetaryAmount applyInDateRange(final BookingBean booking) {

		final MonetaryAmount gross = new DefaultGrossPaymentsSupplier(getDateRange()).apply(booking);
		final MonetaryAmount cleaning = Payments.createMondary(booking.getCleaningFees());
		final MonetaryAmount grossMinusCleaning = gross.subtract(cleaning);
		// if (grossMinusCleaning.getNumber().doubleValue() != 0)
		// System.err.println(booking.getGuest() + " " + grossMinusCleaning);
		return grossMinusCleaning;
	}
}

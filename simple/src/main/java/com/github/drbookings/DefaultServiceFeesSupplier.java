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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Calculates the service fees. That is, the turnover value multiplied by the
 * service fees percent.
 *
 * @author Alexander Kerner
 * @date 2018-07-20
 *
 */
public class DefaultServiceFeesSupplier extends AbstractMonetaryAmountFromBookingsSupplier implements ServiceFeesSupplier {

	private final DefaultTurnoverCalculator turnOverSupplier;

	public DefaultServiceFeesSupplier(final Range<LocalDate> dates) {

		super(dates);
		turnOverSupplier = new DefaultTurnoverCalculator(dates);
	}

	public DefaultServiceFeesSupplier(final YearMonth month) {

		super(month);
		turnOverSupplier = new DefaultTurnoverCalculator(month);
	}

	public DefaultServiceFeesSupplier() {

		super();
		turnOverSupplier = new DefaultTurnoverCalculator();
	}

	@Override
	public MonetaryAmount applyInDateRange(final BookingBean booking) {

		final BigDecimal servicesFeesPercent = BigDecimal.valueOf(booking.getServiceFeesPercent());
		final MonetaryAmount grossMinusCleaning = turnOverSupplier.apply(booking);
		final BigDecimal hh = servicesFeesPercent.divide(BigDecimal.valueOf(100));
		final MonetaryAmount result = grossMinusCleaning.multiply(hh);
		return result;
	}
}

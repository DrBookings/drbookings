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
 * Calculates the total cleaning fees (not costs).
 *
 * @author Alexander Kerner
 * @date 2018-09-18
 *
 */
public class DefaultCleaningFeesSupplier extends AbstractMonetaryAmountFromBookingsSupplier
	implements ServiceFeesSupplier {

    public DefaultCleaningFeesSupplier(final Range<LocalDate> dates) {

	super(dates);
    }

    public DefaultCleaningFeesSupplier(final YearMonth month) {

	super(month);
    }

    public DefaultCleaningFeesSupplier() {

	super();
    }

    @Override
    public MonetaryAmount applyInDateRange(final BookingBean booking) {

	return Payments.createMondary(booking.getCleaningFees());
    }
}

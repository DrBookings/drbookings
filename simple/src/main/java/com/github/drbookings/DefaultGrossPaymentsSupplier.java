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
import java.util.List;

/**
 * Calculates the gross income. That is, payments by guests including cleaning
 * fees. Service fees might be included as well.
 * <ul>
 * <li>For Booking, this includes service fees (once per month 12%).</li>
 * <li>For Airbnb, this excludes service fees (for simplicity, the payout is
 * taken, which is already without service fees).</li>
 * </ul>
 *
 * @author Alexander Kerner
 * @date 2018-08-04
 *
 */
public class DefaultGrossPaymentsSupplier extends AbstractMonetaryAmountFromBookingsSupplier
	implements GrossIncomeSupplier {

    public DefaultGrossPaymentsSupplier() {
	super();

    }

    public DefaultGrossPaymentsSupplier(final Range<LocalDate> dates) {
	super(dates);

    }

    public DefaultGrossPaymentsSupplier(final YearMonth month) {
	super(month);

    }

    @Override
    protected MonetaryAmount applyInDateRange(final BookingBean booking) {
	final MonetaryAmount gross;
	if ((getDateRange() != null) && (booking.getPayments() != null) && !booking.getPayments().isEmpty()) {
	    final List<Payment> paymentsInRange = Payments.getPaymentsInRange(getDateRange(), booking.getPayments());
	    gross = Payments.getSum(paymentsInRange);
	} else {
	    /**
	     * Backward compatibility
	     */

	    gross = Payments.createMondary(booking.getGrossEarnings());

	}
	return gross;
    }

}

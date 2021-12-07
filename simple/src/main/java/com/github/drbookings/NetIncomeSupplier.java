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

import javax.money.MonetaryAmount;

public interface NetIncomeSupplier extends MonetaryAmountFromBookingsSupplier {

	/**
	 * Returns the net income for this booking entry. That is, the booking net
	 * income per night. All income from all booking entries from the same booking
	 * are always the same.
	 *
	 * @param booking
	 *            the {@link BookingEntry} to calculate gross income for
	 * @return gross income for given booking entry
	 */
	default MonetaryAmount apply(final BookingEntry booking) {

		final double numberOfNights = booking.getElement().getNumberOfNights();
		return apply(booking.getElement()).divide(numberOfNights);
	}
}

package com.github.drbookings.model;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.ui.BookingEntry;

public class DefaultNetEarningsCalculator implements NetEarningsCalculator {

	public DefaultNetEarningsCalculator() {

	}

	@Override
	public Number apply(final Booking booking) {
		double result = booking.getGrossEarnings();
		result -= (booking.getGrossEarnings() - booking.getCleaningFees()) * booking.getServiceFeesPercent() / 100.0;
		result -= booking.getServiceFee();
		// if (booking.getCleaning() != null) {
		// result -= booking.getCleaning().getCleaningCosts();
		// }
		return result;
	}

	@Override
	public Number apply(final BookingEntry booking) {
		final double numberOfNights = booking.getElement().getNumberOfNights();
		// if (booking.getElement().getGuest().getName().contains("Peter")) {
		// System.err.println(apply(booking.getElement()) + " " + numberOfNights
		// + " "
		// + apply(booking.getElement()).doubleValue() / numberOfNights);
		// }
		return apply(booking.getElement()).doubleValue() / numberOfNights;
	}

}

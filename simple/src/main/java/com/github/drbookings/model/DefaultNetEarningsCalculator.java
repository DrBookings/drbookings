/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

package com.github.drbookings.model;

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.ui.BookingEntry;

public class DefaultNetEarningsCalculator implements NetEarningsCalculator {

	public DefaultNetEarningsCalculator() {

	}

	@Override
    public Number apply(final BookingBean booking) {
		double result = booking.getGrossEarnings();
		result -= (booking.getGrossEarnings() - booking.getCleaningFees()) * booking.getServiceFeesPercent() / 100.0;
		result -= booking.getServiceFee();
		// if (booking.getCleaning() != null) {
		// result -= booking.getCleaning().getCleaningCosts();
		// }
		return result;
	}

    /**
     * Returns the earnings for this booking entry. That is, the booking earnings per night. All earnings from all
     * booking entries from the same booking are always the same.
     *
     * @param booking the {@link BookingEntry} to calculate net earnings for
     * @return net earnings for given booking entry
     */
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

package com.github.drbookings.model;

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
		if (booking.getCleaning() != null) {
			result -= booking.getCleaning().getCleaningCosts();
		}
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

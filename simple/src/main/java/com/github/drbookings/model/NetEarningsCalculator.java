package com.github.drbookings.model;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.ui.BookingEntry;

public interface NetEarningsCalculator extends java.util.function.Function<Booking, Number> {

	@Override
	Number apply(Booking booking);

	Number apply(BookingEntry booking);

}

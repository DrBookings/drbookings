package com.github.drbookings.model;

import java.util.Collection;
import java.util.function.Function;

import com.github.drbookings.model.data.Booking;

public class NightCounter implements Function<Collection<? extends Booking>, Number> {

	public NightCounter() {

	}

	@Override
	public Number apply(final Collection<? extends Booking> bookings) {
		return null;
	}

}

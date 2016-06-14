package com.github.drbookings.core.datamodel.impl;

import java.time.LocalDate;

import com.github.drbookings.core.datamodel.api.Booking;

public class BookingBean implements Booking {

	private final LocalDate checkIn, checkOut;

	private final String guestNames;

	private final String id;

	public BookingBean(final String id, final LocalDate checkIn, final LocalDate checkOut, final String names) {
		this.id = id;
		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.guestNames = names;
	}

	@Override
	public LocalDate getCheckIn() {
		return checkIn;
	}

	@Override
	public LocalDate getCheckOut() {
		return checkOut;
	}

	public String getGuestNames() {
		return guestNames;
	}

	@Override
	public String getID() {
		return id;
	}

}

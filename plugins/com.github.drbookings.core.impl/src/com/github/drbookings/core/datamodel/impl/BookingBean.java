package com.github.drbookings.core.datamodel.impl;

import java.time.LocalDate;
import java.util.Collection;

import com.github.drbookings.core.datamodel.api.Booking;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;

public class BookingBean extends BookingIdentifiableBean implements Booking {

	private final SimpleObjectProperty<LocalDate> checkIn = new SimpleObjectProperty<LocalDate>();

	private final SimpleObjectProperty<LocalDate> checkOut = new SimpleObjectProperty<LocalDate>();

	public BookingBean(final String id, final LocalDate checkIn, final LocalDate checkOut) {
		super(id);
		this.checkIn.set(checkIn);
		this.checkOut.set(checkOut);

	}

	public BookingBean(final String id, final LocalDate checkIn, final LocalDate checkOut,
			final Collection<? extends String> names) {
		this(id, checkIn, checkOut);
		setGuestNames(names);
	}

	@Override
	public ObservableValue<LocalDate> getCheckIn() {
		return checkIn;
	}

	@Override
	public ObservableValue<LocalDate> getCheckOut() {
		return checkOut;
	}

	@Override
	public BookingBean setStatus(final String newStatus) {

		super.setStatus(newStatus);
		return this;
	}

}

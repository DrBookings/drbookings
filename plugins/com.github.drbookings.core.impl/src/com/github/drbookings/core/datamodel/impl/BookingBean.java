package com.github.drbookings.core.datamodel.impl;

import java.time.LocalDate;

import com.github.drbookings.core.datamodel.api.Booking;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

public class BookingBean implements Booking {

	private final SimpleObjectProperty<LocalDate> checkIn = new SimpleObjectProperty<LocalDate>();
	private final SimpleObjectProperty<LocalDate> checkOut = new SimpleObjectProperty<LocalDate>();

	private final SimpleStringProperty guestNames = new SimpleStringProperty();

	private final SimpleStringProperty id = new SimpleStringProperty();

	public BookingBean(final String id, final LocalDate checkIn, final LocalDate checkOut) {
		this.id.set(id);
		this.checkIn.set(checkIn);
		this.checkOut.set(checkOut);

	}

	public BookingBean(final String id, final LocalDate checkIn, final LocalDate checkOut, final String names) {
		this(id, checkIn, checkOut);
		this.guestNames.set(names);
	}

	@Override
	public ObservableValue<LocalDate> getCheckIn() {
		return checkIn;
	}

	@Override
	public ObservableValue<LocalDate> getCheckOut() {
		return checkOut;
	}

	public ObservableStringValue getGuestNames() {
		return guestNames;
	}

	@Override
	public ObservableStringValue getId() {
		return id;
	}

}

package com.github.drbookings.core.datamodel.impl;

import java.time.LocalDate;
import java.util.List;

import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.api.BookingDay;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BookingDayBean extends BookingIdentifiableBean implements BookingDay, ListChangeListener<Booking> {

	private final ObservableList<Booking> bookings;

	private final SimpleObjectProperty<List<Booking>> bookingsValue;

	private final SimpleObjectProperty<LocalDate> date = new SimpleObjectProperty<LocalDate>();

	public BookingDayBean(final String id, final LocalDate date, final Booking initialBooking) {
		super(id);
		this.bookings = FXCollections.observableArrayList(initialBooking);
		this.bookings.addListener(this);
		this.bookingsValue = new SimpleObjectProperty<>(bookings);
		this.date.setValue(date);
	}

	@Override
	public ObservableList<Booking> getBookings() {
		return bookings;
	}

	@Override
	public ObservableValue<List<Booking>> getBookingsValue() {
		return bookingsValue;
	}

	@Override
	public SimpleObjectProperty<LocalDate> getDate() {
		return date;
	}

	@Override
	public void onChanged(final javafx.collections.ListChangeListener.Change<? extends Booking> c) {
		bookingsValue.setValue(bookings);
	}

	public BookingDayBean setDate(final LocalDate date) {
		this.date.setValue(date);
		return this;
	}

}

package com.github.drbookings.core.datamodel.impl;

import java.time.LocalDate;
import java.util.Collection;

import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.api.BookingDay;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Bookings {

	private final ObservableList<Booking> bookings;

	public Bookings(final Collection<? extends Booking> bookings) {
		super();
		this.bookings = FXCollections.observableArrayList(bookings);
	}

	public ObservableList<BookingDay> toBookingDays() {
		final ObservableList<BookingDay> result = FXCollections.observableArrayList();
		for (final Booking b : bookings) {
			for (LocalDate date = b.getCheckIn().getValue(); date
					.isBefore(b.getCheckOut().getValue()); date = date.plusDays(1)) {
				result.add(new BookingDayBean(b.getId(), date, b));
			}
		}
		return result;
	}

	public LocalDate getDateFirst() {
		return bookings.stream().sorted((b1, b2) -> b1.getCheckIn().getValue().compareTo(b2.getCheckIn().getValue()))
				.findFirst().get().getCheckIn().getValue();
	}

	public LocalDate getDateLast() {
		return bookings.stream().sorted((b1, b2) -> b1.getCheckOut().getValue().compareTo(b2.getCheckOut().getValue()))
				.reduce((a, b) -> b).get().getCheckOut().getValue();
	}

}

package com.github.drbookings.service.bookingmanager;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.core.api.BookingManager;
import com.github.drbookings.core.datamodel.api.Booking;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookingManagerDefault implements BookingManager {

	private final static Logger log = LoggerFactory.getLogger(BookingManagerDefault.class);
	private final ObservableList<Booking> bookings = FXCollections
			.synchronizedObservableList(FXCollections.observableArrayList());

	@Override
	public BookingManagerDefault addAllBookings(final Collection<? extends Booking> bookings) {
		this.bookings.addAll(bookings);
		return this;

	}

	@Override
	public BookingManagerDefault addBooking(final Booking booking) {
		this.bookings.add(booking);
		return this;
	}

	@Override
	public ObservableList<Booking> getBookings() {
		return bookings;
	}

}

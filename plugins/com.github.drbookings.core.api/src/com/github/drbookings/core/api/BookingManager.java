package com.github.drbookings.core.api;

import java.util.Collection;

import com.github.drbookings.core.datamodel.api.Booking;

import javafx.collections.ObservableList;

public interface BookingManager {

	BookingManager addAllBookings(Collection<? extends Booking> result);

	BookingManager addBooking(Booking booking);

	ObservableList<Booking> getBookings();

}

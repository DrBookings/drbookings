package com.github.drbookings.core.api;

import java.util.Collection;

import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.api.BookingDay;

import javafx.collections.ObservableList;

public interface BookingManager {

	BookingManager addAllBookings(Collection<? extends Booking> booking);

	BookingManager addBooking(Booking booking);

	ObservableList<BookingDay> getBookings();

}

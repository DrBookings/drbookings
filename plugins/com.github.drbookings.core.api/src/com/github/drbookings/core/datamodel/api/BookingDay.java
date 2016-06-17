package com.github.drbookings.core.datamodel.api;

import java.time.LocalDate;
import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public interface BookingDay extends BookingIdentifiable {

	ObservableList<Booking> getBookings();

	ObservableValue<List<Booking>> getBookingsValue();

	ObservableValue<LocalDate> getDate();

}

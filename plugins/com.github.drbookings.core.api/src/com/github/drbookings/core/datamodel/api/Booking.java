package com.github.drbookings.core.datamodel.api;

import java.time.LocalDate;

import javafx.beans.value.ObservableValue;

public interface Booking extends BookingIdentifiable {

	ObservableValue<LocalDate> getCheckIn();

	ObservableValue<LocalDate> getCheckOut();

}

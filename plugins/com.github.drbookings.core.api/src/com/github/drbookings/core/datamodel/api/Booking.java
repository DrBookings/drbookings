package com.github.drbookings.core.datamodel.api;

import java.time.LocalDate;

import javafx.beans.value.ObservableStringValue;
import javafx.beans.value.ObservableValue;

public interface Booking {

	ObservableValue<LocalDate> getCheckIn();

	ObservableValue<LocalDate> getCheckOut();

	ObservableStringValue getId();

}

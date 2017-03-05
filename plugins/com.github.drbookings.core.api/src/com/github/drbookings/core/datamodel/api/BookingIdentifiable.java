package com.github.drbookings.core.datamodel.api;

import java.util.List;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public interface BookingIdentifiable {

	ObservableList<String> getGuestNames();

	ObservableValue<List<String>> getGuestNamesValue();

	ObservableValue<String> getId();

	ObservableValue<String> getStatus();

}

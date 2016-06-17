package com.github.drbookings.core.datamodel.api;

import javafx.collections.ObservableList;

public interface BookingIdentifiable {

	ObservableList<String> getGuestNames();

	String getId();

}

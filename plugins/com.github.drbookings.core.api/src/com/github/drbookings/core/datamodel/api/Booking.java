package com.github.drbookings.core.datamodel.api;

import java.time.LocalDate;

public interface Booking {

	LocalDate getCheckIn();

	LocalDate getCheckOut();

	String getID();

}

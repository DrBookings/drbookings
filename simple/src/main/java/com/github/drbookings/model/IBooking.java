package com.github.drbookings.model;

import com.github.drbookings.model.data.BookingOrigin;

public interface IBooking {

	float getGrossEarnings();

	float getNetEarnings();

	BookingOrigin getBookingOrigin();

}

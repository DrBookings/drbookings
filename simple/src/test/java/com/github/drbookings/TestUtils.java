package com.github.drbookings;

import java.time.LocalDate;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;

public class TestUtils {

	public static Guest getTestGuest() {
		return new Guest("testGuest");
	}

	public static Room getTestRoom() {
		return new Room("testRoom");
	}

	public static BookingOrigin getTestBookingOrigin() {
		return new BookingOrigin("TestBookingOrigin");
	}

	public static Booking getTestBooking(final LocalDate checkIn, final LocalDate checkOut) {
		return new Booking(getTestGuest(), getTestRoom(), getTestBookingOrigin(), checkIn, checkOut);
	}

	public static Booking getTestBooking() {
		return getTestBooking(LocalDate.now(), LocalDate.now().plusDays(1));
	}

}

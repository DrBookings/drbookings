package com.github.drbookings;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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

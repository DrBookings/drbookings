package com.github.drbookings.model.data;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.github.drbookings.DateRange;
import com.github.drbookings.ui.BookingEntry;

public class Bookings {

	public static long countNights(final Collection<? extends Booking> bookings) {
		return bookings.stream().filter(b -> !b.isSplitBooking()).mapToLong(b -> b.getNumberOfNights()).sum();
	}

	public static double getServiceFeePercentAmount(final Booking booking) {
		return (booking.getGrossEarnings() - booking.getCleaningFees()) * booking.getServiceFeesPercent() / 100.0;
	}

	public static Collection<BookingEntry> toEntries(final Collection<Booking> bookings) {
		final Collection<BookingEntry> result = new ArrayList<>();
		for (final Booking b : bookings) {
			for (final LocalDate d : new DateRange(b.getCheckIn(), b.getCheckOut())) {
				result.add(new BookingEntry(d, b));
			}
		}
		return result;
	}

	public static Collection<BookingEntry> toEntries(final Booking... bookings) {
		return toEntries(Arrays.asList(bookings));
	}
}

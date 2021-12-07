/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
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
 */

package com.github.drbookings;

import java.time.LocalDate;
import java.util.SortedSet;

public class CleaningNeededEvaluator {

    private final ReadOnlyCleaningData cleaningData;

    private final ReadOnlyBookingData bookingData;

    public CleaningNeededEvaluator(final ReadOnlyCleaningData data, final ReadOnlyBookingData bookingData) {
	cleaningData = data;
	this.bookingData = bookingData;
    }

    ReadOnlyCleaningData getCleaningData() {
	return cleaningData;
    }

    ReadOnlyBookingData getBookingData() {
	return bookingData;
    }

    public boolean evaluate(final RoomBean room) {

	final BookingEntryPair booking = room.getBookingEntry();

	if (booking == null)
	    return false;

	if (!booking.hasCheckOut())
	    // no checkout, no cleaning
	    return false;

	CleaningEntry ce = room.getCleaningEntry();

	if (ce != null)
	    return false;

	// TODO if there is no such cleaning, check time period until next booking
	final LocalDate thisDate = room.getDate();

	if (getCleaningData() != null) {
	    final SortedSet<LocalDate> dates = getCleaningData().allDates();

	    final SortedSet<LocalDate> tailDates = dates.tailSet(thisDate);

	    for (final LocalDate d : tailDates) {
		ce = getCleaningData().get(d, room.getName());
		if (getBookingData() != null) {
		    final BookingEntryPair be = getBookingData().get(d, room.getName());
		    if (be != null)
			// booking found before cleaning
			// TODO: passt das so?
			return true;
		}
		if (ce != null)
		    // cleaning found before booking
		    return false;
	    }
	}

	return true;
    }

}

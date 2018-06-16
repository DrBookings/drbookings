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

package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Callable;

import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.ui.CleaningEntry;

public class CleaningNeededCalculator implements Callable<Boolean> {

    public static final LocalDate DEFAULT_TIME_OUT_DATE = LocalDate.now();

    public static boolean cleaningNeeded(final LocalDate date, final String roomName, final DrBookingsDataImpl data,
	    LocalDate timeOutDate) {

	if (data.getBookingEntries().isEmpty()) {
	    return false;
	}

	if (timeOutDate == null) {
	    final Optional<LocalDate> firstDate = data.getFirstBookingDate();
	    // must be present, since data not empty
	    timeOutDate = firstDate.get();

	}

	// check for timeout
	if (isTimeOut(date, timeOutDate)) {
	    return false;
	}
	final Optional<CleaningEntry> ceo = data.getCleaningEntry(roomName, date);
	if (ceo.isPresent()) {
	    // cleaning present for this room at this date
	    return false;
	} else {
	    // go forward in time and find either a cleaning(return false) or a check-out (
	    // return true).
	    LocalDate dateToCheck = date.minusDays(1);
	    while (!dateToCheck.equals(data.getLastBookingDate().get())) {
		dateToCheck = dateToCheck.plusDays(1);
		final Optional<CleaningEntry> ceo2 = data.getCleaningEntry(roomName, dateToCheck);
		if (ceo.isPresent()) {
		    // cleaning found
		    return false;
		}
		final Optional<BookingEntryPair> beo2 = data.getBookingEntryPair(roomName, dateToCheck);
		if (beo2.isPresent()) {
		    // booking found
		    // consider only check-outs
		    if (beo2.get().hasCheckOut()) {
			return true;
		    } else {
			// a non-check-out booking entry
			return false;
		    }
		}
	    }
	    // last date reached
	    return false;
	}
    }

    static boolean isTimeOut(final LocalDate date, final LocalDate timeOutDate) {
	return date.isBefore(timeOutDate);
    }

    private final DrBookingsDataImpl data;
    private final LocalDate date;

    private final String roomName;

    private LocalDate timeOutDate = DEFAULT_TIME_OUT_DATE;

    public CleaningNeededCalculator(final LocalDate date, final String roomName, final DrBookingsDataImpl data) {
	this.data = data;
	this.date = date;
	this.roomName = roomName;
    }

    @Override
    public Boolean call() throws Exception {
	return cleaningNeeded(date, roomName, data, timeOutDate);
    }

    public LocalDate getTimeOutDate() {
	return timeOutDate;
    }

    public CleaningNeededCalculator setTimeOutDate(final LocalDate timeOutDate) {
	this.timeOutDate = timeOutDate;
	return this;
    }
}

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

import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.ui.CleaningEntry;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Callable;

public class CleaningNeededCalculator implements Callable<Boolean> {

    public static final LocalDate DEFAULT_TIME_OUT_DATE = LocalDate.now();
    private final DrBookingsDataImpl data;
    private final LocalDate date;
    private final String roomName;
    private LocalDate timeOutDate = DEFAULT_TIME_OUT_DATE;

    public CleaningNeededCalculator(LocalDate date, String roomName, DrBookingsDataImpl data) {
        this.data = data;
        this.date = date;
        this.roomName = roomName;
    }

    static boolean isTimeOut(LocalDate date, LocalDate timeOutDate) {
        return date.isBefore(timeOutDate);
    }

    public static boolean cleaningNeeded(LocalDate date, String roomName, DrBookingsDataImpl data,
        LocalDate timeOutDate) {

        if (data.getBookingEntries().isEmpty()) {
            return false;
        }

        if (timeOutDate == null) {
            Optional<LocalDate> firstDate = data.getFirstBookingDate();
            // must be present, since data not empty
            timeOutDate = firstDate.get();

        }

        // check for timeout
        if (isTimeOut(date, timeOutDate)) {
            return false;
        }
        Optional<CleaningEntry> ceo = data.getCleaningEntry(roomName, date);
        if (ceo.isPresent()) {
            // cleaning present for this room at this date
            return false;
        } else {
            // go forward in time and find either a cleaning(return false) or a check-out ( return true).
            LocalDate dateToCheck = date.minusDays(1);
            while (!dateToCheck.equals(data.getLastBookingDate().get())) {
                dateToCheck = dateToCheck.plusDays(1);
                Optional<CleaningEntry> ceo2 = data.getCleaningEntry(roomName, dateToCheck);
                if (ceo.isPresent()) {
                    // cleaning found
                    return false;
                }
                Optional<BookingEntryPair> beo2 = data.getBookingEntryPair(roomName, dateToCheck);
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

    public LocalDate getTimeOutDate() {
        return timeOutDate;
    }

    public CleaningNeededCalculator setTimeOutDate(LocalDate timeOutDate) {
        this.timeOutDate = timeOutDate;
        return this;
    }

    @Override
    public Boolean call() throws Exception {
        return cleaningNeeded(date, roomName, data, timeOutDate);
    }
}

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

package com.github.drbookings.model;

import com.github.drbookings.model.data.DrBookingsData;
import com.github.drbookings.ui.CleaningEntry;
import java.time.LocalDate;
import java.util.List;

public class UnifiedDataAccess extends DataAccess {

    protected final LocalDate date;

    protected List<RoomEntry> roomEntries;

    protected List<CleaningEntry> cleaningEntries;

    protected List<BookingEntry> bookingEntries;

    public UnifiedDataAccess( LocalDate date,DrBookingsData data) {
        super(data);
        this.date = date;
    }

    public List<RoomEntry> getRoomEntries() {
        return roomEntries;
    }

    public List<CleaningEntry> getCleaningEntries() {
        return cleaningEntries;
    }

    @Override
    public UnifiedDataAccess init() {
        super.init();

        roomEntries = data.getRoomEntries(date);
        cleaningEntries = data.getCleaningEntries(date);
        bookingEntries = data.getBookingEntries(date);

        return this;
    }

    public List<BookingEntry> getBookingEntries() {
        return bookingEntries;
    }
}

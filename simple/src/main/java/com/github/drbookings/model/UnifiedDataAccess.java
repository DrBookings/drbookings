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

import java.time.LocalDate;
import java.util.List;

import com.github.drbookings.model.data.DrBookingsDataImpl;
import com.github.drbookings.ui.CleaningEntry;

public class UnifiedDataAccess extends DataAccess {

    protected final LocalDate date;

    protected List<RoomEntry> roomEntries;

    protected List<CleaningEntry> cleaningEntries;

    protected List<BookingEntryPair> bookingEntries;

    public UnifiedDataAccess(final LocalDate date, final DrBookingsDataImpl data) {
	super(data);
	this.date = date;
    }

    public List<BookingEntryPair> getBookingEntries() {
	return bookingEntries;
    }

    public List<CleaningEntry> getCleaningEntries() {
	return cleaningEntries;
    }

    public List<RoomEntry> getRoomEntries() {
	return roomEntries;
    }

    @Override
    public UnifiedDataAccess init() {
	super.init();

	roomEntries = data.getRoomEntries(date);
	cleaningEntries = data.getCleaningEntries(date);
	bookingEntries = data.getBookingEntryPairs(date);

	return this;
    }
}

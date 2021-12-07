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

import com.github.drbookings.exception.AlreadyBusyException;

import java.time.LocalDate;
import java.util.Objects;

public class SimpleCleaningData extends AbstractDateData<CleaningEntry> implements CleaningData {

    /**
     * Use provider to keep name unique.
     */
    private final CleaningFactory cleaningFactory;
    /**
     * Use provider to keep name unique.
     */
    private final RoomFactory roomFactory;

    public SimpleCleaningData(final CleaningFactory cleaningFactory, final RoomFactory roomFactory) {

	super();
	this.cleaningFactory = cleaningFactory;
	this.roomFactory = roomFactory;
    }

    /**
     * for testing.
     */
    public SimpleCleaningData() {

	cleaningFactory = CleaningFactory.getInstance();
	roomFactory = RoomFactory.getInstance();
    }

    @Deprecated
    public CleaningEntry createAndAddCleaning(final String id, final String cleaningName, final LocalDate date,
	    final BookingBean booking, final boolean black) throws AlreadyBusyException {

	CleaningEntry cleaningEntry = getEntries()
		.get(getMultiKey(Objects.requireNonNull(booking).getRoom().getName(), date));
	if (cleaningEntry == null) {
	    cleaningEntry = createNewCleaningEntry(id, getOrCreateCleaning(cleaningName), date, booking, black);
	    getEntries().put(getMultiKey(booking.getRoom().getName(), date), cleaningEntry);
	} else
	    throw new AlreadyBusyException("There is already a cleaning at " + date + " for "
		    + booking.getRoom().getName() + ": " + cleaningEntry);
	return cleaningEntry;
    }

    public CleaningEntry createNewCleaningEntry(final String id, final Cleaning cleaning, final LocalDate date,
	    final BookingBean booking, final boolean black) {

	return new CleaningEntry(id, date, booking, cleaning, black);
    }

    CleaningFactory getCleaningProvider() {

	return cleaningFactory;
    }

    public Cleaning getOrCreateCleaning(final String name) {

	final Cleaning room = cleaningFactory.getOrCreateElement(name);
	return room;
    }

    @Override
    public synchronized CleaningEntry add(final LocalDate date, final String name, final String roomName,
	    final boolean black) {

	Objects.requireNonNull(date);
	Objects.requireNonNull(name);
	Objects.requireNonNull(roomName);
	final Room r = roomFactory.getOrCreateElement(roomName);
	final Cleaning c = cleaningFactory.getOrCreateElement(name);
	final CleaningEntry ce = new CleaningEntry(date, c, r, black);
	add(ce);
	return ce;
    }
}

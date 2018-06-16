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
import java.util.List;
import java.util.Optional;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.model.exception.OverbookingException;
import com.github.drbookings.ui.CleaningEntry;

import javafx.beans.property.BooleanProperty;

public interface DataProvider {

    List<BookingEntry> addBooking(BookingBean b) throws OverbookingException;

    CleaningEntry addCleaning(String name, LocalDate date, String room) throws AlreadyBusyException;

    boolean cleaningNeededFor(String name, LocalDate date);

    BooleanProperty cleaningsChangedProperty();

    BookingBean createBooking(String bookingId, LocalDate checkInDate, LocalDate checkOutDate, String guestName,
	    String roomName, String source);

    Optional<BookingEntry> getAfter(BookingEntry e);

    Optional<BookingEntry> getBefore(BookingEntry e);

    List<BookingEntry> getBookingEntries();

    Optional<BookingEntryPair> getBookingEntryPair(String name, LocalDate date);

    List<BookingEntryPair> getBookingEntryPairs();

    List<BookingBean> getBookings();

    List<CleaningEntry> getCleaningEntries();

    Optional<CleaningEntry> getCleaningEntry(String name, LocalDate date);
}

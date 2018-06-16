/*
 * DrBookings
 * Copyright (C) 2016 - 2018 Alexander Kerner
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
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

public interface DrBookingsData {

    CleaningEntry addCleaning(String name, LocalDate date, String roomName) throws AlreadyBusyException;

    boolean cleaningNeededFor(String name, LocalDate date);

    BooleanProperty cleaningsChangedProperty();

    BookingBean createAndAddBooking(LocalDate checkInDate, LocalDate checkOutDate, String guestName, String roomName,
	    String source) throws OverbookingException;

    BookingBean createBooking(String bookingId, LocalDate checkInDate, LocalDate checkOutDate, String guestName,
	    String roomName, String source);

    Optional<BookingEntryPair> getAfter(BookingEntry e, int numDays);

    Optional<BookingEntryPair> getBefore(BookingEntry e, int numDays);

    List<BookingEntry> getBookingEntries();

    Optional<BookingEntryPair> getBookingEntryPair(String name, LocalDate date);

    List<BookingEntryPair> getBookingEntryPairs();

    List<BookingBean> getBookings();

    List<CleaningEntry> getCleaningEntries();

    Optional<CleaningEntry> getCleaningEntry(String name, LocalDate date);

    /**
     * Returns the check-in-, the stay- or the check-out booking, in that order, if
     * present.
     *
     * @param bookingsThatDay
     *            the {@link BookingEntryPair} from which the last booking should be
     *            extracted
     * @return the last {@link BookingEntry}
     */
    Optional<BookingEntry> getFirstBookingEntry(Optional<BookingEntryPair> bookingsThatDay);

    /**
     * Returns the check-in-, the stay- or the check-out booking, in that order, if
     * present.
     *
     * @param roomName
     * @param date
     * @return the last {@link BookingEntry}
     */
    Optional<BookingEntry> getFirstBookingEntry(String roomName, LocalDate date);

    /**
     * Returns the check-out-, the stay- or the check-in booking, in that order, if
     * present.
     *
     * @param bookingsThatDay
     *            the {@link BookingEntryPair} from which the last booking should be
     *            extracted
     * @return the last {@link BookingEntry}
     */
    Optional<BookingEntry> getLastBookingEntry(Optional<BookingEntryPair> bookingsThatDay);

    /**
     * Returns the check-out-, the stay- or the check-in booking, in that order, if
     * present.
     *
     * @param roomName
     * @param date
     * @return the last {@link BookingEntry}
     */
    Optional<BookingEntry> getLastBookingEntry(String roomName, LocalDate date);

    /**
     * Returns an {@link Optional} holding a {@link BookingEntryPair} for the same
     * {@link Room} and one day after the given {@link BookingEntry}.
     *
     * @param e
     *            BookingEntry
     * @return
     */
    Optional<BookingEntryPair> getOneDayAfter(BookingEntry e);

    /**
     * Returns an {@link Optional} holding a {@link BookingEntryPair} for the same
     * {@link Room} and one day before the given {@link BookingEntry}.
     *
     * @param e
     *            BookingEntry
     * @return
     */
    Optional<BookingEntryPair> getOneDayBefore(BookingEntry e);

    void removeCleaning(LocalDate date, String roomName);

    void setCleaning(String name, LocalDate date, String roomName);
}

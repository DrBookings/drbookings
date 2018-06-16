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
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.exception.OverbookingException;
import com.github.drbookings.ui.beans.RoomBean;

/**
 * A container to hold a {@link RoomBean room's} {@link BookingEntry booking
 * entries}. A room has zero, one (stay-over or check-in) or two (check-in,
 * check-out) booking entries.
 *
 * @author Alexander Kerner
 *
 */
public class BookingEntryPair {

    public static BookingEntryPair newCheckInStayPair(final BookingEntry checkIn, final BookingEntry stay) {
	return new BookingEntryPair(checkIn, stay, null);
    }

    public static BookingEntryPair newStayCheckOutPair(final BookingEntry stay, final BookingEntry checkOut) {
	return new BookingEntryPair(null, stay, checkOut);
    }

    private BookingEntry checkIn;

    private BookingEntry checkOut;

    private BookingEntry stay;

    public BookingEntryPair(final BookingEntry entry) {
	Objects.requireNonNull(entry, "BookingEntry must not be null");
	if (entry.isCheckIn()) {
	    this.checkIn = entry;
	} else if (entry.isCheckOut()) {
	    this.checkOut = entry;
	} else if (entry.isStay()) {
	    this.stay = entry;
	} else {
	    throw new IllegalArgumentException();
	}
    }

    BookingEntryPair(final BookingEntry checkIn, final BookingEntry stay, final BookingEntry checkOut) {
	validateDates(checkIn, stay, checkOut);
	validateRooms(checkIn, stay, checkOut);
	this.checkIn = checkIn;
	this.stay = stay;
	this.checkOut = checkOut;
    }

    public void addBooking(final BookingEntry be) throws OverbookingException {
	checkOverbooking(be);
	checkConstrains(be);
	if (be.isCheckIn()) {
	    this.checkIn = be;
	} else if (be.isCheckOut()) {
	    this.checkOut = be;
	} else if (be.isStay()) {
	    this.stay = be;
	} else {
	    throw new RuntimeException();
	}
    }

    public Set<String> bookingOriginNamesView() {
	return bookingOriginsView().stream().map(e -> e.getName()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<BookingOrigin> bookingOriginsView() {
	return toList().stream().map(e -> e.getElement().getBookingOrigin())
		.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * {@link Room} and date must match for all {@link BookingEntry booking
     * entries}.
     *
     * @throws IllegalArgumentException
     *             if room or date does not match
     */
    private void checkConstrains(final BookingEntry be) throws IllegalArgumentException {
	if (!be.getDate().equals(getDate())) {
	    throw new IllegalArgumentException("Dates do not match");
	}
	if (!be.getRoom().equals(getRoom())) {
	    throw new IllegalArgumentException("Rooms do not match");
	}
    }

    private void checkOverbooking(final BookingEntry be) throws OverbookingException {
	if (be.isCheckIn() && this.hasCheckIn()) {
	    throw new OverbookingException("This entry pair has already a check-in");
	}
	if (be.isCheckOut() && this.hasCheckOut()) {
	    throw new OverbookingException("This entry pair has already a check-out");
	}
	if (!be.isStay() && this.hasStay()) {
	    throw new OverbookingException("This entry pair has already a stay");
	}
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	return Objects.equals(obj, this);
    }

    public BookingEntry getCheckIn() {
	return checkIn;
    }

    public BookingEntry getCheckOut() {
	return checkOut;
    }

    LocalDate getDate() {
	if (hasCheckIn()) {
	    return checkIn.getDate();
	}
	if (hasCheckOut()) {
	    return checkOut.getDate();
	}
	return stay.getDate();
    }

    public BookingEntry getFirst() {
	if (checkIn != null) {
	    return checkIn;
	}
	if (stay != null) {
	    return stay;
	}
	if (checkOut != null) {
	    return checkOut;
	}
	throw new RuntimeException();
    }

    public BookingEntry getLast() {
	if (checkOut != null) {
	    return checkOut;
	}
	if (stay != null) {
	    return stay;
	}
	if (checkIn != null) {
	    return checkIn;
	}
	throw new RuntimeException();
    }

    Room getRoom() {
	if (hasCheckIn()) {
	    return checkIn.getRoom();
	}
	if (hasCheckOut()) {
	    return checkOut.getRoom();
	}
	return stay.getRoom();
    }

    public BookingEntry getStay() {
	return stay;
    }

    public Set<String> guestNameView() {
	return guestView().stream().map(e -> e.getName()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Set<Guest> guestView() {
	return toList().stream().map(e -> e.getElement().getGuest())
		.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public boolean hasCheckIn() {
	return checkIn != null;
    }

    public boolean hasCheckOut() {
	return checkOut != null;
    }

    public boolean hasGuest(final Guest guest) {
	return toList().stream().map(b -> b.getElement().getGuest()).filter(g -> g.equals(guest)).findFirst()
		.isPresent();
    }

    @Override
    public int hashCode() {
	return Objects.hash(getCheckIn(), getStay(), getCheckOut());
    }

    public boolean hasRoom(final Room room) {
	return toList().stream().map(b -> b.getElement().getRoom()).filter(g -> g.equals(room)).findFirst().isPresent();
    }

    public boolean hasStay() {
	return stay != null;
    }

    public short size() {
	short result = 0;
	if (checkIn != null) {
	    result++;
	}
	if (stay != null) {
	    result++;
	}
	if (checkOut != null) {
	    result++;
	}
	return result;
    }

    public List<BookingEntry> toList() {
	final List<BookingEntry> result = new ArrayList<>(2);
	if (checkIn != null) {
	    result.add(checkIn);
	}
	if (stay != null) {
	    result.add(stay);
	}
	if (checkOut != null) {
	    result.add(checkOut);
	}
	return result;
    }

    public Stream<BookingEntry> toStream() {
	return toList().stream();
    }

    private void validateDates(final BookingEntry checkIn, final BookingEntry stay, final BookingEntry checkOut) {
	if (checkIn != null && stay != null && !checkIn.getDate().equals(stay.getDate())) {
	    throw new IllegalArgumentException("Dates do not match");
	}
	if (checkOut != null && stay != null && !checkOut.getDate().equals(stay.getDate())) {
	    throw new IllegalArgumentException("Dates do not match");
	}
	if (checkOut != null && checkIn != null && !checkOut.getDate().equals(checkIn.getDate())) {
	    throw new IllegalArgumentException("Dates do not match");
	}
    }

    private void validateRooms(final BookingEntry checkIn, final BookingEntry stay, final BookingEntry checkOut) {
	if (checkIn != null && stay != null && !checkIn.getRoom().equals(stay.getRoom())) {
	    throw new IllegalArgumentException("Rooms do not match");
	}
	if (checkOut != null && stay != null && !checkOut.getRoom().equals(stay.getRoom())) {
	    throw new IllegalArgumentException("Rooms do not match");
	}
	if (checkOut != null && checkIn != null && !checkOut.getRoom().equals(checkIn.getRoom())) {
	    throw new IllegalArgumentException("Rooms do not match");
	}
    }
}

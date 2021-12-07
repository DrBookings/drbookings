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
import java.util.List;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import com.github.drbookings.exception.OverbookingException;

public class DrBookingsDataCore {

    /**
     * Use provider to keep name unique.
     */
    private final BookingOriginProvider bookingOriginProvider;

    /**
     * Use provider to keep name unique.
     */
    private final GuestProvider guestProvider;

    /**
     * Use provider to keep name unique.
     */

    private final RoomFactory roomProvider;
    /**
     * (Room name, Date) -> Value
     */
    protected final MultiKeyMap<Object, BookingEntryPair> bookingEntries;

    /**
     * (Room name, Date) -> Value
     */
    protected final MultiKeyMap<Object, RoomEntry> roomEntries;

    public DrBookingsDataCore() {
	roomProvider = RoomFactory.getInstance();
	guestProvider = new GuestProvider();
	bookingOriginProvider = new BookingOriginProvider();
	roomEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
	bookingEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
    }

    public BookingBean createAndAddBooking(final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) throws OverbookingException {
	return createAndAddBooking(null, checkInDate, checkOutDate, guestName, roomName, source);
    }

    public BookingBean createAndAddBooking(final String id, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String originName) throws OverbookingException {
	final BookingBean newBooking = createBooking(id, checkInDate, checkOutDate, guestName, roomName, originName);
	addBooking(newBooking);
	return newBooking;
    }

    BookingBean createBooking(final String bookingId, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) {
	final BookingBeanFactory f = new BookingBeanFactory(getGuestProvider(), getRoomProvider(),
		getBookingOriginProvider());
	return f.createBooking(bookingId, checkInDate, checkOutDate, guestName, roomName, source);
    }

    List<BookingEntry> addBooking(final BookingBean bb) throws OverbookingException {

	final List<BookingEntry> bes = Bookings.toEntries(bb);
	// none of those booking entries must be here already
	for (final BookingEntry be : bes) {
	    final Room room = be.getRoom();
	    final LocalDate date = be.getDate();
	    BookingEntryPair be2 = bookingEntries.get(getBookingEntryMultiKey(room.getName(), date));
	    if (be2 != null) {
		be2.addBooking(be);
	    } else {
		be2 = new BookingEntryPair(be);
		final BookingEntryPair oldVal = bookingEntries.put(getBookingEntryMultiKey(room.getName(), date), be2);
		if (oldVal != null)
		    throw new RuntimeException();
	    }

	}
	return bes;
    }

    protected RoomEntry createNewRoomEntry(final Room room, final LocalDate date) {

	return new RoomEntry(date, room);
    }

    protected MultiKey<Object> getBookingEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    BookingOriginProvider getBookingOriginProvider() {
	return bookingOriginProvider;
    }

    GuestProvider getGuestProvider() {
	return guestProvider;
    }

    Room getOrCreateRoom(final String name, final LocalDate date) {

	final Room room = roomProvider.getOrCreateElement(name);
	return room;
    }

    RoomEntry getOrCreateRoomEntry(final Room room, final LocalDate date) {

	return getOrCreateRoomEntry(room.getName(), date);
    }

    RoomEntry getOrCreateRoomEntry(final String roomName, final LocalDate date) {

	RoomEntry roomEntry = roomEntries.get(getRoomEntryMultiKey(roomName, date));
	if (roomEntry == null) {
	    roomEntry = createNewRoomEntry(getOrCreateRoom(roomName, date), date);
	    final RoomEntry oldVal = roomEntries.put(getRoomEntryMultiKey(roomName, date), roomEntry);
	    if (oldVal != null)
		throw new RuntimeException();
	}
	return roomEntry;
    }

    protected MultiKey<Object> getRoomEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    RoomFactory getRoomProvider() {
	return roomProvider;
    }

}

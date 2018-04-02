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

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.RoomEntry;
import com.github.drbookings.model.data.manager.BookingOriginProvider;
import com.github.drbookings.model.data.manager.CleaningProvider;
import com.github.drbookings.model.data.manager.GuestProvider;
import com.github.drbookings.model.data.manager.RoomProvider;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.model.exception.OverbookingException;
import com.github.drbookings.ui.CleaningEntry;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.apache.commons.lang3.StringUtils;

public class DrBookingsData {

    /**
     * Use provider to keep name unique.
     */
    private final CleaningProvider cleaningProvider;

    /**
     * Use provider to keep name unique.
     */
    private final GuestProvider guestProvider;

    /**
     * Use provider to keep name unique.
     */
    private final RoomProvider roomProvider;

    /**
     * Use provider to keep name unique.
     */
    private final BookingOriginProvider bookingOriginProvider;

    /**
     * (Room name, Date) -> Entry
     */
    private final MultiKeyMap<Object, RoomEntry> roomEntries;

    protected MultiKey<Object> getRoomEntryMultiKey(String roomName, LocalDate date) {
        return new MultiKey<>(roomName, date);
    }

    /**
     * (Room name, Date) -> Entry
     */
    private final MultiKeyMap<Object, CleaningEntry> cleaningEntries;

    protected MultiKey<Object> getCleaningEntryMultiKey(String roomName,
        LocalDate date) {
        return new MultiKey<>(roomName, date);
    }

    /**
     * (Room name, Date) -> Entry
     */
    private final MultiKeyMap<Object, BookingEntry> bookingEntries;

    protected MultiKey<Object> getBookingEntryMultiKey(String roomName, LocalDate date) {
        return new MultiKey<>(roomName, date);
    }

    public DrBookingsData() {
        roomProvider = new RoomProvider();
        guestProvider = new GuestProvider();
        cleaningProvider = new CleaningProvider();
        bookingOriginProvider = new BookingOriginProvider();
        roomEntries = new MultiKeyMap<>();
        cleaningEntries = new MultiKeyMap<>();
        bookingEntries = new MultiKeyMap<>();
    }

    protected RoomEntry createNewRoomEntry(Room room, LocalDate date) {
        return new RoomEntry(date, room);
    }

    public synchronized Room getOrCreateRoom(String name, LocalDate date) {
        Room room = roomProvider.getOrCreateElement(name);
        return room;
    }

    protected CleaningEntry createNewCleaningEntry(Cleaning cleaning, LocalDate date, Room room) {
        return new CleaningEntry(getOrCreateRoomEntry(room, date), cleaning);
    }

    public synchronized CleaningEntry createCleaningEntry(String cleaningName, LocalDate date,
        String roomName)
        throws AlreadyBusyException {
        CleaningEntry cleaningEntry = cleaningEntries
            .get(getCleaningEntryMultiKey(roomName, date));
        if (cleaningEntry == null) {
            cleaningEntry = createNewCleaningEntry(getOrCreateCleaning(cleaningName, date), date,
                getOrCreateRoom(roomName, date));
            cleaningEntries
                .put(getCleaningEntryMultiKey(roomName, date), cleaningEntry);
        } else {
            throw new AlreadyBusyException(
                "There is already a cleaning at " + date + " for " + roomName + ": "
                    + cleaningEntry);
        }
        return cleaningEntry;
    }

    public synchronized List<CleaningEntry> getCleaningEntries() {
        return Collections.unmodifiableList(new ArrayList<>(cleaningEntries.values()));
    }

    public synchronized List<BookingEntry> getBookingEntries() {
        return Collections.unmodifiableList(new ArrayList<>(bookingEntries.values()));
    }

    public synchronized Optional<RoomEntry> getRoomEntry(String roomName, LocalDate date){
        return Optional.ofNullable(roomEntries.get(getRoomEntryMultiKey(roomName, date)));
    }

    public synchronized Optional<BookingEntry> getBookingEntry(String roomName, LocalDate date) {
        return Optional.ofNullable(bookingEntries.get(getBookingEntryMultiKey(roomName, date)));
    }

    public Optional<CleaningEntry> getCleaningEntry(String roomName, LocalDate date) {
        return Optional.ofNullable(cleaningEntries.get(getCleaningEntryMultiKey(roomName, date)));
    }

    public List<RoomEntry> getRoomEntries(LocalDate date) {
        List<RoomEntry> result = new ArrayList<>();
        Set<String> roomNames = getRoomNames();
        for(String roomName : roomNames){
            RoomEntry re = roomEntries.get(getRoomEntryMultiKey(roomName, date));
            if(re != null){
                result.add(re);
            }
        }
        return result;
    }

    public List<CleaningEntry> getCleaningEntries(LocalDate date) {
        List<CleaningEntry> result = new ArrayList<>();
        Set<String> roomNames = getRoomNames();
        for(String roomName : roomNames){
            CleaningEntry ce = cleaningEntries.get(getCleaningEntryMultiKey(roomName, date));
            if(ce != null){
                result.add(ce);
            }
        }
        return result;
    }

    public List<BookingEntry> getBookingEntries(LocalDate date) {
        List<BookingEntry> result = new ArrayList<>();
        Set<String> roomNames = getRoomNames();
        for(String roomName : roomNames){
            BookingEntry ce = bookingEntries.get(getBookingEntryMultiKey(roomName, date));
            if(ce != null){
                result.add(ce);
            }
        }
        return result;
    }

    protected Set<String> getRoomNames(){
        return roomEntries.keySet().stream().map(k -> (String)k.getKey(0)).collect(Collectors.toSet());
    }

    public synchronized List<BookingBean> getBookings() {
        return Collections
            .unmodifiableList(new ArrayList<>(getBookingEntries().stream().map(be -> be.getElement()).collect(
                Collectors.toSet())));
    }

    public synchronized RoomEntry getOrCreateRoomEntry(String roomName, LocalDate date) {
        RoomEntry roomEntry = roomEntries.get(getRoomEntryMultiKey(roomName, date));
        if (roomEntry == null) {
            roomEntry = createNewRoomEntry(getOrCreateRoom(roomName, date), date);
            RoomEntry oldVal = roomEntries.put(getRoomEntryMultiKey(roomName, date), roomEntry);
            if (oldVal != null) {
                throw new RuntimeException();
            }
        }
        return roomEntry;
    }



    public synchronized RoomEntry getOrCreateRoomEntry(Room room, LocalDate date) {
        return getOrCreateRoomEntry(room.getName(), date);
    }

    public synchronized Cleaning getOrCreateCleaning(String name, LocalDate date) {
        Cleaning room = cleaningProvider.getOrCreateElement(name);
        return room;
    }

    public synchronized void addBooking(BookingBean bb) throws OverbookingException {
        List<BookingEntry> bes = Bookings.toEntries(bb);
        // none of those booking entries must be here already
        for (BookingEntry be : bes) {
            Room room = be.getRoom();
            LocalDate date = be.getDate();
            BookingEntry be2 = bookingEntries.get(getBookingEntryMultiKey(room.getName(), date));
            if (be2 != null) {
                throw new OverbookingException("Cannot add " + bb + ", already busy with " + be2);
            }
            BookingEntry oldVal = bookingEntries
                .put(getBookingEntryMultiKey(room.getName(), date), be);
            if (oldVal != null) {
                throw new RuntimeException();
            }
        }
    }

    public synchronized BookingBean createAndAddBooking(final String id,
        final LocalDate checkInDate,
        final LocalDate checkOutDate, final String guestName, final String
        roomName, final String originName) throws OverbookingException {
        BookingBean newBooking = createBooking(id, checkInDate, checkOutDate, guestName, roomName, originName);
        addBooking(newBooking);
        return newBooking;
    }

    /**
     * Does not add the newly created {@link BookingBean} to this {@code DrBookingsData}!
     *
     * @return the newly created {@link BookingBean}
     */
    public synchronized BookingBean createBooking(final String id, final LocalDate checkInDate,
        final LocalDate checkOutDate, final String guestName, final String
        roomName, final String originName) {
        Objects.requireNonNull(checkInDate);
        Objects.requireNonNull(checkOutDate);
        if (StringUtils.isBlank(guestName)) {
            throw new IllegalArgumentException("No guest name given");
        }
        if (StringUtils.isBlank(roomName)) {
            throw new IllegalArgumentException("No room name given");
        }
        final Guest guest = guestProvider.getOrCreateElement(guestName);
        final Room room = roomProvider.getOrCreateElement(roomName);
        final BookingOrigin bookingOrigin = bookingOriginProvider.getOrCreateElement(originName);
        final BookingBean booking = new BookingBean(id, guest, room, bookingOrigin, checkInDate,
            checkOutDate);
        return booking;
    }


    public Optional<LocalDate> getFirstBookingDate() {
        List<BookingEntry> bes = new ArrayList<>(getBookingEntries());
        if(bes.isEmpty()){
            return Optional.empty();
        }
        Collections.sort(bes);
        return Optional.of(bes.get(0).getDate());
    }

    public Optional<LocalDate> getLastBookingDate() {
        List<BookingEntry> bes = new ArrayList<>(getBookingEntries());
        if(bes.isEmpty()){
            return Optional.empty();
        }
        Collections.sort(bes, Comparator.reverseOrder());
        return Optional.of(bes.get(0).getDate());
    }
}

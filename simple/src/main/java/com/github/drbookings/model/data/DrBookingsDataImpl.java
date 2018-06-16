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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.model.RoomEntry;
import com.github.drbookings.model.data.manager.BookingOriginProvider;
import com.github.drbookings.model.data.manager.CleaningProvider;
import com.github.drbookings.model.data.manager.GuestProvider;
import com.github.drbookings.model.data.manager.RoomProvider;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.model.exception.OverbookingException;
import com.github.drbookings.ui.CleaningEntry;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author Alexander Kerner
 */
public class DrBookingsDataImpl implements DrBookingsData {

    private static final Logger logger = LoggerFactory.getLogger(DrBookingsDataImpl.class);

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
     * (Room name, Date) -> Value
     */
    private final MultiKeyMap<Object, RoomEntry> roomEntries;
    /**
     * (Room name, Date) -> Value
     */
    private final MultiKeyMap<Object, CleaningEntry> cleaningEntries;
    /**
     * (Room name, Date) -> Value
     */
    private final MultiKeyMap<Object, BookingEntryPair> bookingEntries;
    /**
     * A dummy property to listen on for cleaning changes. The actual value does not
     * mean anything. Maybe refactor to UI.
     */
    private final BooleanProperty cleaningsChanged;
    /**
     * A dummy property to listen on for booking changes. The actual value does not
     * mean anything. Maybe refactor to UI.
     */
    private final BooleanProperty bookingsChanged;

    public DrBookingsDataImpl() {

	roomProvider = new RoomProvider();
	guestProvider = new GuestProvider();
	cleaningProvider = new CleaningProvider();
	bookingOriginProvider = new BookingOriginProvider();
	roomEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
	cleaningEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
	bookingEntries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
	cleaningsChanged = new SimpleBooleanProperty(false);
	bookingsChanged = new SimpleBooleanProperty(false);
    }

    protected MultiKey<Object> getRoomEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    protected MultiKey<Object> getCleaningEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    protected MultiKey<Object> getBookingEntryMultiKey(final String roomName, final LocalDate date) {

	return new MultiKey<>(roomName, date);
    }

    @Override
    public BooleanProperty cleaningsChangedProperty() {

	return cleaningsChanged;
    }

    public BooleanProperty bookingsChangedProperty() {

	return bookingsChanged;
    }

    protected RoomEntry createNewRoomEntry(final Room room, final LocalDate date) {

	return new RoomEntry(date, room);
    }

    /**
     * Returns an {@link Optional} holding a {@link BookingEntry} that has the same
     * {@link Room} and is one day plus to the given {@link BookingEntry}.
     *
     * @param e
     *            BookingEntry
     * @return
     */
    @Override
    public Optional<BookingEntryPair> getOneDayAfter(final BookingEntry e) {

	return getAfter(e, 1);
    }

    @Override
    public Optional<BookingEntry> getFirstBookingEntry(final String roomName, final LocalDate date) {

	return getFirstBookingEntry(getBookingEntryPair(roomName, date));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BookingEntry> getLastBookingEntry(final String roomName, final LocalDate date) {

	return getLastBookingEntry(getBookingEntryPair(roomName, date));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BookingEntry> getFirstBookingEntry(final Optional<BookingEntryPair> bookingsThatDay) {

	if (bookingsThatDay.isPresent()) {
	    return Optional.of(bookingsThatDay.get().getFirst());
	}
	return Optional.empty();
    }

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public Optional<BookingEntry> getLastBookingEntry(final Optional<BookingEntryPair> bookingsThatDay) {

	if (bookingsThatDay.isPresent()) {
	    return Optional.of(bookingsThatDay.get().getLast());
	}
	return Optional.empty();
    }

    /**
     * Returns an {@link Optional} holding a {@link BookingEntry} that has the same
     * {@link Room} and is any days later than the given {@link BookingEntry}. If
     * there is more than one booking (e.g. check-in and check-out), the later one
     * is returned (check-out).
     *
     * @param e
     *            BookingEntry
     * @return
     */
    public Optional<BookingEntry> getNext(final BookingEntry e) {

	if (isEmtpy()) {
	    return Optional.empty();
	}
	LocalDate date = e.getDate();
	final LocalDate last = getLastBookingDate().get();
	Optional<BookingEntryPair> bookingsThatDay = null;
	while ((date.isBefore(last) || date.equals(last))
		&& (bookingsThatDay == null || !bookingsThatDay.isPresent())) {
	    date = date.plusDays(1);
	    bookingsThatDay = getBookingEntryPair(e.getRoom().getName(), date);
	}
	if (bookingsThatDay.isPresent()) {
	    return Optional.of(bookingsThatDay.get().getFirst());
	}
	return Optional.empty();
    }

    @Override
    public Optional<BookingEntryPair> getAfter(final BookingEntry e, final int numDays) {

	final LocalDate plusX = e.getDate().plusDays(numDays);
	return getBookingEntryPair(e.getRoom().getName(), plusX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<BookingEntryPair> getOneDayBefore(final BookingEntry e) {

	final LocalDate minusOneDay = e.getDate().minusDays(1);
	return getBookingEntryPair(e.getRoom().getName(), minusOneDay);
    }

    @Override
    public Optional<BookingEntryPair> getBefore(final BookingEntry e, final int numDays) {

	final LocalDate plusOneDay = e.getDate().plusDays(1);
	return getBookingEntryPair(e.getRoom().getName(), plusOneDay);
    }

    public synchronized Room getOrCreateRoom(final String name, final LocalDate date) {

	final Room room = roomProvider.getOrCreateElement(name);
	return room;
    }

    protected CleaningEntry createNewCleaningEntry(final Cleaning cleaning, final LocalDate date, final Room room) {

	return new CleaningEntry(getOrCreateRoomEntry(room, date), cleaning);
    }

    @Override
    public synchronized CleaningEntry addCleaning(final String cleaningName, final LocalDate date,
	    final String roomName) throws AlreadyBusyException {

	CleaningEntry cleaningEntry = cleaningEntries.get(getCleaningEntryMultiKey(roomName, date));
	if (cleaningEntry == null) {
	    cleaningEntry = createNewCleaningEntry(getOrCreateCleaning(cleaningName, date), date,
		    getOrCreateRoom(roomName, date));
	    cleaningEntries.put(getCleaningEntryMultiKey(roomName, date), cleaningEntry);
	    notifyCleaningsChanged();
	} else {
	    throw new AlreadyBusyException(
		    "There is already a cleaning at " + date + " for " + roomName + ": " + cleaningEntry);
	}
	return cleaningEntry;
    }

    private void notifyCleaningsChanged() {

	// simply toggle the value
	cleaningsChanged.set(!cleaningsChanged.get());
    }

    private void notifyBookingsChanged() {

	// simply toggle the value
	bookingsChanged.set(!bookingsChanged.get());
    }

    @Override
    public synchronized List<CleaningEntry> getCleaningEntries() {

	return Collections.unmodifiableList(new ArrayList<>(cleaningEntries.values()));
    }

    @Override
    public synchronized List<BookingEntryPair> getBookingEntryPairs() {

	return Collections.unmodifiableList(new ArrayList<>(bookingEntries.values()));
    }

    @Override
    public synchronized List<BookingEntry> getBookingEntries() {

	return Collections.unmodifiableList(
		getBookingEntryPairs().stream().flatMap(e -> e.toList().stream()).collect(Collectors.toList()));
    }

    public synchronized Optional<RoomEntry> getRoomEntry(final String roomName, final LocalDate date) {

	return Optional.ofNullable(roomEntries.get(getRoomEntryMultiKey(roomName, date)));
    }

    @Override
    public synchronized Optional<BookingEntryPair> getBookingEntryPair(final String roomName, final LocalDate date) {

	return Optional.ofNullable(bookingEntries.get(getBookingEntryMultiKey(roomName, date)));
    }

    @Override
    public Optional<CleaningEntry> getCleaningEntry(final String roomName, final LocalDate date) {

	return Optional.ofNullable(cleaningEntries.get(getCleaningEntryMultiKey(roomName, date)));
    }

    public List<RoomEntry> getRoomEntries(final LocalDate date) {

	final List<RoomEntry> result = new ArrayList<>();
	final Set<String> roomNames = getRoomNames();
	for (final String roomName : roomNames) {
	    final RoomEntry re = roomEntries.get(getRoomEntryMultiKey(roomName, date));
	    if (re != null) {
		result.add(re);
	    }
	}
	return result;
    }

    public List<CleaningEntry> getCleaningEntries(final LocalDate date) {

	final List<CleaningEntry> result = new ArrayList<>();
	final Set<String> roomNames = getRoomNames();
	for (final String roomName : roomNames) {
	    final CleaningEntry ce = cleaningEntries.get(getCleaningEntryMultiKey(roomName, date));
	    if (ce != null) {
		result.add(ce);
	    }
	}
	return result;
    }

    public List<BookingEntryPair> getBookingEntryPairs(final LocalDate date) {

	final List<BookingEntryPair> result = new ArrayList<>();
	final Set<String> roomNames = getRoomNames();
	for (final String roomName : roomNames) {
	    final BookingEntryPair ce = bookingEntries.get(getBookingEntryMultiKey(roomName, date));
	    if (ce != null) {
		result.add(ce);
	    }
	}
	return result;
    }

    protected Set<String> getRoomNames() {

	return roomEntries.keySet().stream().map(k -> (String) k.getKey(0)).collect(Collectors.toSet());
    }

    @Override
    public synchronized List<BookingBean> getBookings() {

	return Collections.unmodifiableList(new ArrayList<>(getBookingEntryPairs().stream()
		.flatMap(e -> e.toList().stream()).map(be -> be.getElement()).collect(Collectors.toSet())));
    }

    public synchronized RoomEntry getOrCreateRoomEntry(final String roomName, final LocalDate date) {

	RoomEntry roomEntry = roomEntries.get(getRoomEntryMultiKey(roomName, date));
	if (roomEntry == null) {
	    roomEntry = createNewRoomEntry(getOrCreateRoom(roomName, date), date);
	    final RoomEntry oldVal = roomEntries.put(getRoomEntryMultiKey(roomName, date), roomEntry);
	    if (oldVal != null) {
		throw new RuntimeException();
	    }
	}
	return roomEntry;
    }

    public synchronized RoomEntry getOrCreateRoomEntry(final Room room, final LocalDate date) {

	return getOrCreateRoomEntry(room.getName(), date);
    }

    public synchronized Cleaning getOrCreateCleaning(final String name, final LocalDate date) {

	final Cleaning room = cleaningProvider.getOrCreateElement(name);
	return room;
    }

    public synchronized List<BookingEntry> addBooking(final BookingBean bb) throws OverbookingException {

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
		if (oldVal != null) {
		    throw new RuntimeException();
		}
	    }
	    notifyBookingsChanged();
	}
	return bes;
    }

    public synchronized BookingBean createAndAddBooking(final String id, final LocalDate checkInDate,
	    final LocalDate checkOutDate, final String guestName, final String roomName, final String originName)
	    throws OverbookingException {

	final BookingBean newBooking = new BookingBeanFactory(guestProvider, roomProvider, bookingOriginProvider)
		.createBooking(id, checkInDate, checkOutDate, guestName, roomName, originName);
	addBooking(newBooking);
	return newBooking;
    }

    public Optional<LocalDate> getFirstBookingDate() {

	final List<BookingEntry> bes = new ArrayList<>(getBookingEntries());
	if (bes.isEmpty()) {
	    return Optional.empty();
	}
	Collections.sort(bes);
	return Optional.of(bes.get(0).getDate());
    }

    public Optional<LocalDate> getLastBookingDate() {

	final List<BookingEntry> bes = new ArrayList<>(getBookingEntries());
	if (bes.isEmpty()) {
	    return Optional.empty();
	}
	Collections.sort(bes, Comparator.reverseOrder());
	return Optional.of(bes.get(0).getDate());
    }

    @Override
    public boolean cleaningNeededFor(final String roomName, final LocalDate date) {

	// check, if there is a booking at all
	final Optional<BookingEntryPair> bookingEntryPairOptional = getBookingEntryPair(roomName, date);
	if (bookingEntryPairOptional.isPresent()) {
	    final BookingEntryPair bookingEntryPair = bookingEntryPairOptional.get();
	    if (!bookingEntryPair.hasCheckOut()) {
		// no checkout, no cleaning
		return false;
	    }
	}
	// check for cleaning entry that room that day
	final Optional<CleaningEntry> cleaningEntryOptional = getCleaningEntry(roomName, date);
	if (cleaningEntryOptional.isPresent()) {
	    return false;
	}
	// if there is no such cleaning, check time period until next booking
	return true;
    }

    public boolean isEmtpy() {

	return bookingEntries.isEmpty() && cleaningEntries.isEmpty();
    }

    public void clear() {

	bookingEntries.clear();
	cleaningEntries.clear();
	roomEntries.clear();
    }

    public void removeBooking(final BookingBean booking) {

	final List<BookingEntry> bes = Bookings.toEntries(booking);
	for (final BookingEntry be : bes) {
	    bookingEntries.remove(getBookingEntryMultiKey(be.getRoom().getName(), be.getDate()));
	}
    }

    public void removeBookings(final Collection<? extends BookingBean> bookings) {

	bookings.forEach(e -> removeBooking(e));
    }

    public void removeCleaning(final CleaningEntry cleaningEntry) {

	cleaningEntries.remove(getCleaningEntryMultiKey(cleaningEntry.getRoom().getName(), cleaningEntry.getDate()));
    }

    @Override
    public BookingBean createBooking(final String bookingId, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) {

	final BookingBeanFactory f = new BookingBeanFactory(guestProvider, roomProvider, bookingOriginProvider);
	return f.createBooking(bookingId, checkInDate, checkOutDate, guestName, roomName, source);
    }

    @Override
    public void setCleaning(final String name, final LocalDate date, final String roomName) {
	final CleaningEntry newCe = createNewCleaningEntry(getOrCreateCleaning(name, date), date,
		getOrCreateRoom(roomName, date));
	final CleaningEntry ce = cleaningEntries.put(getCleaningEntryMultiKey(roomName, date), newCe);
	if (ce != null && logger.isInfoEnabled()) {
	    logger.info(ce + " replaced by " + newCe);
	}
    }

    @Override
    public void removeCleaning(final LocalDate date, final String roomName) {
	cleaningEntries.remove(getCleaningEntryMultiKey(roomName, date));

    }

    @Override
    public BookingBean createAndAddBooking(final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) throws OverbookingException {
	final BookingBean bb = createBooking(null, checkInDate, checkOutDate, guestName, roomName, source);
	addBooking(bb);
	return bb;
    }
}

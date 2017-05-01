package com.github.drbookings.model.data.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.CleaningEntry;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainManager {

    private static final Logger logger = LoggerFactory.getLogger(MainManager.class);

    private final RoomProvider roomProvider;

    private final GuestProvider guestProvider;

    private final CleaningProvider cleaningProvider;

    private final BookingOriginProvider bookingOriginProvider;

    private final Multimap<LocalDate, CleaningEntry> cleaningEntries;

    private final ListProperty<CleaningEntry> cleaningEntriesList = new SimpleListProperty<>(
	    FXCollections.observableArrayList());

    private final Multimap<LocalDate, BookingEntry> bookingEntries;

    private final List<Booking> bookings;

    private final ListProperty<DateBean> uiData;

    private final Map<LocalDate, DateBean> uiDataMap;

    public MainManager() {
	roomProvider = new RoomProvider();
	guestProvider = new GuestProvider();
	cleaningProvider = new CleaningProvider();
	bookingOriginProvider = new BookingOriginProvider();
	bookings = new ArrayList<>();
	cleaningEntries = ArrayListMultimap.create();
	bookingEntries = ArrayListMultimap.create();
	uiData = new SimpleListProperty<>(FXCollections.observableArrayList(DateBean.extractor()));
	uiDataMap = new LinkedHashMap<>();

    }

    public synchronized Booking addBooking(final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String originName) throws OverbookingException {
	return addBooking(null, checkInDate, checkOutDate, guestName, roomName, originName);
    }

    public synchronized Booking addBooking(final String id, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String originName) throws OverbookingException {
	Objects.requireNonNull(checkInDate);
	Objects.requireNonNull(checkOutDate);
	if (guestName == null || guestName.length() < 1) {
	    throw new IllegalArgumentException("No guest name given");
	}
	if (roomName == null || roomName.length() < 1) {
	    throw new IllegalArgumentException("No room name given");
	}
	final Guest guest = guestProvider.getOrCreateElement(guestName);
	final Room room = roomProvider.getOrCreateElement(roomName);
	final BookingOrigin bookingOrigin = bookingOriginProvider.getOrCreateElement(originName);
	final Booking booking = new Booking(id, guest, room, bookingOrigin, checkInDate, checkOutDate);
	return addBooking(booking);
    }

    protected synchronized Booking addBooking(final Booking booking) {
	bookings.add(booking);
	for (final LocalDate d : new DateRange(booking.getCheckIn(), booking.getCheckOut())) {
	    addBookingEntry(d, booking);
	}
	return booking;
    }

    protected synchronized void addBookingEntry(final LocalDate date, final Booking booking) {
	final BookingEntry bookingEntry = new BookingEntry(date, booking);
	bookingEntries.put(date, bookingEntry);
	addUiDataBooking(bookingEntry);
    }

    public synchronized void addCleaning(final LocalDate date, final String cleaningName, final String roomName) {
	if (cleaningName == null || cleaningName.trim().length() == 0) {
	    throw new IllegalArgumentException();
	}
	final Cleaning cleaning = cleaningProvider.getOrCreateElement(cleaningName);
	final Room room = roomProvider.getOrCreateElement(roomName);
	final CleaningEntry cleaningEntry = new CleaningEntry(date, room, cleaning);
	cleaningEntries.put(date, cleaningEntry);
	cleaningEntriesList.add(cleaningEntry);
	addUiDataCleaning(cleaningEntry);
    }

    private void addUiDataBooking(final BookingEntry bookingEntry) {
	final Room room = bookingEntry.getRoom();
	DateBean db = uiDataMap.get(bookingEntry.getDate());
	if (db == null) {
	    db = new DateBean(bookingEntry.getDate(), this);
	    addDateBean(db);
	}
	db.getRoom(room.getName()).addBookingEntry(bookingEntry);
	fillMissing();
    }

    private void fillMissing() {
	final List<LocalDate> dates = new ArrayList<>(uiDataMap.keySet());
	Collections.sort(dates);
	final Collection<LocalDate> toAdd = new HashSet<>();
	LocalDate last = null;
	for (final LocalDate d : dates) {
	    if (last != null) {
		if (d.equals(last.plusDays(1))) {
		    // ok
		} else {
		    toAdd.addAll(new DateRange(last.plusDays(1), d.minusDays(1)).toList());
		}
	    }
	    last = d;
	}
	for (final LocalDate d : toAdd) {
	    addDateBean(new DateBean(d, this));
	}

    }

    private void addDateBean(final DateBean db) {
	uiData.add(db);
	uiDataMap.put(db.getDate(), db);
    }

    private void addUiDataCleaning(final CleaningEntry cleaningEntry) {
	final Room room = cleaningEntry.getRoom();
	DateBean db = uiDataMap.get(cleaningEntry.getDate());
	if (db == null) {
	    db = new DateBean(cleaningEntry.getDate(), this);
	    uiData.add(db);
	    uiDataMap.put(db.getDate(), db);
	}
	db.getRoom(room.getName()).setCleaningEntry(cleaningEntry);
    }

    private void removeUiDataCleaning(final CleaningEntry cleaningEntry) {
	final Room room = cleaningEntry.getRoom();
	final DateBean db = uiDataMap.get(cleaningEntry.getDate());
	if (db == null) {
	    if (logger.isWarnEnabled()) {
		logger.warn("No date entry found for " + cleaningEntry);
	    }
	} else {
	    db.getRoom(room.getName()).setCleaningEntry(null);
	}
    }

    public synchronized void applyFilter(final String guestNameFilterString) {
	for (final DateBean db : uiData) {
	    for (final RoomBean rb : db.getRooms()) {
		rb.setBookingFilterString(guestNameFilterString);
	    }
	}
    }

    public synchronized List<BookingEntry> getBookingEntries() {
	return Collections.unmodifiableList(new ArrayList<>(bookingEntries.values()));
    }

    public synchronized List<Booking> getBookings() {
	return bookings;
    }

    public synchronized Collection<BookingEntry> getBookingEntries(final LocalDate date) {
	return Collections.unmodifiableCollection(bookingEntries.get(date));
    }

    public synchronized Collection<CleaningEntry> getCleaningEntries() {
	return Collections.unmodifiableCollection(cleaningEntries.values());
    }

    public synchronized ObservableList<DateBean> getUIData() {
	return uiData;
    }

    public synchronized boolean needsCleaning(final String roomName, final LocalDate date) {
	final List<LocalDate> dates = new ArrayList<>(cleaningEntries.asMap().keySet());
	if (!dates.isEmpty()) {
	    Collections.sort(dates, Comparator.reverseOrder());
	    final LocalDate lastDate = dates.get(0);
	    if (lastDate.isBefore(date)) {
		return true;
	    }
	    for (final LocalDate d : new DateRange(date, lastDate)) {

		if (!date.equals(d)) {
		    final Collection<BookingEntry> bookings = bookingEntries.get(d).stream()
			    .filter(b -> b.getRoom().getName().equals(roomName)).collect(Collectors.toList());
		    if (!bookings.isEmpty()) {
			// another booking in between
			return true;
		    }
		}

		final Collection<CleaningEntry> e = cleaningEntries.get(d);
		if (e != null && !e.isEmpty() && CleaningEntry.roomNameView(e).contains(roomName)) {
		    return false;
		}
	    }
	}
	return true;

    }

    public synchronized boolean removeBooking(final Booking booking) {
	return removeBookings(Arrays.asList(booking));
    }

    public synchronized boolean removeBookings(final List<Booking> bookings) {
	this.bookings.removeAll(bookings);
	for (final Iterator<BookingEntry> it = bookingEntries.values().iterator(); it.hasNext();) {
	    final BookingEntry be = it.next();
	    if (bookings.contains(be.getElement())) {
		it.remove();
	    }
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Booking entries now " + bookingEntries.size());
	}
	final boolean result = this.bookings.removeAll(bookings);
	if (logger.isDebugEnabled()) {
	    logger.debug("Bookings now " + this.bookings.size());
	}
	removeUiDataBooking(bookings);
	return result;
    }

    private void removeUiDataBooking(final Collection<? extends Booking> bookings) {
	for (final DateBean e : uiData) {
	    for (final RoomBean r : e.getRooms()) {
		for (final Iterator<BookingEntry> it = r.getBookingEntries().iterator(); it.hasNext();) {
		    final BookingEntry be = it.next();
		    for (final Booking bb : bookings) {
			if (be.getElement().equals(bb)) {
			    if (logger.isDebugEnabled()) {
				logger.debug("Removing " + be);
			    }
			    it.remove();
			}
		    }
		}
	    }
	}
    }

    public ListProperty<CleaningEntry> cleaningEntriesListProperty() {
	return this.cleaningEntriesList;
    }

    public List<CleaningEntry> getCleaningEntriesList() {
	return this.cleaningEntriesListProperty().get();
    }

    public void setCleaningEntriesList(final Collection<? extends CleaningEntry> cleaningEntriesList) {
	this.cleaningEntriesListProperty().setAll(cleaningEntriesList);
    }

    public void removeCleaning(final CleaningEntry cleaningEntry) {

	System.err.println(cleaningEntries.size());
	cleaningEntries.values().remove(cleaningEntry);
	System.err.println(cleaningEntries.size());
	cleaningEntriesList.remove(cleaningEntry);
	removeUiDataCleaning(cleaningEntry);
    }

}

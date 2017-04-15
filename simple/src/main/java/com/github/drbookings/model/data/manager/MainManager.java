package com.github.drbookings.model.data.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.controller.BookingEntry;
import com.github.drbookings.ui.controller.CleaningEntry;
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
	addUiData(bookingEntry);
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
	addUiData(cleaningEntry);
    }

    private void addUiData(final BookingEntry bookingEntry) {
	final Room room = bookingEntry.getRoom();
	DateBean db = uiDataMap.get(bookingEntry.getDate());
	if (db == null) {
	    db = new DateBean(bookingEntry.getDate(), this);
	    uiData.add(db);
	    uiDataMap.put(db.getDate(), db);
	}
	db.getRoom(room.getName()).addBookingEntry(bookingEntry);
    }

    private void addUiData(final CleaningEntry cleaningEntry) {
	final Room room = cleaningEntry.getRoom();
	DateBean db = uiDataMap.get(cleaningEntry.getDate());
	if (db == null) {
	    db = new DateBean(cleaningEntry.getDate(), this);
	    uiData.add(db);
	    uiDataMap.put(db.getDate(), db);
	}
	db.getRoom(room.getName()).setCleaningEntry(cleaningEntry);
    }

    public synchronized void applyGuestNameFilter(final String guestNameFilterString) {
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
	bookingEntries.values().removeAll(bookings);
	if (logger.isDebugEnabled()) {
	    logger.debug("Booking entries now " + bookingEntries);
	}
	final boolean result = this.bookings.removeAll(bookings);
	if (logger.isDebugEnabled()) {
	    logger.debug("Bookings now " + bookings);
	}
	removeUiData(bookings);
	return result;
    }

    private void removeUiData(final Booking booking) {
	removeUiData(Arrays.asList(booking));
    }

    private void removeUiData(final Collection<? extends Booking> bookings) {
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
	cleaningEntries.values().remove(cleaningEntry);
	cleaningEntriesList.remove(cleaningEntry);
    }

}

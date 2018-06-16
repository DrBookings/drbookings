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
package com.github.drbookings.model.data.manager;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.DrBookingsData;
import com.github.drbookings.model.data.DrBookingsDataImpl;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.model.exception.OverbookingException;
import com.github.drbookings.ui.CleaningEntry;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * @author Alexander Kerner
 */
public class MainManager implements DrBookingsData {

    private static class InstanceHolder {

	private static final MainManager instance = new MainManager();
    }

    private static final Logger logger = LoggerFactory.getLogger(MainManager.class);

    public static MainManager getInstance() {

	return InstanceHolder.instance;
    }

    /**
     * The 'main' UI data, i.e., the data elements/ rows in the main table.
     */
    private final ListProperty<DateBean> uiData;

    /**
     * Access-by-date map.
     */
    private final Map<LocalDate, DateBean> uiDataMap;

    /**
     * Booking, Cleaning, .. data.
     */
    private final DrBookingsDataImpl data;

    public MainManager() {

	data = new DrBookingsDataImpl();
	uiData = new SimpleListProperty<>(FXCollections.observableArrayList(DateBean.extractor()));
	uiDataMap = new LinkedHashMap<>();
    }

    public List<BookingEntry> addBooking(final BookingBean b) throws OverbookingException {

	final List<BookingEntry> bookings = getData().addBooking(b);
	bookings.forEach(b2 -> addUiDataBooking(b2));
	return bookings;
    }

    @Override
    public CleaningEntry addCleaning(final String name, final LocalDate date, final String room)
	    throws AlreadyBusyException {

	return getData().addCleaning(name, date, room);
    }

    private void addDateBean(final DateBean db) {

	uiData.add(db);
	uiDataMap.put(db.getDate(), db);
    }

    public void addUiDataBooking(final BookingEntry bookingEntry) {

	final Room room = bookingEntry.getRoom();
	DateBean db = uiDataMap.get(bookingEntry.getDate());
	if (db == null) {
	    db = new DateBean(bookingEntry.getDate(), this);
	    addDateBean(db);
	}
	fillMissing();
    }

    public synchronized void applyFilter(final String guestNameFilterString) {

	// uiData.addAll(filteredDates);
	// filteredDates.clear();
	for (final Iterator<DateBean> it = uiData.iterator(); it.hasNext();) {
	    final DateBean db = it.next();
	    for (final RoomBean rb : db.getRooms()) {
		rb.setBookingFilterString(guestNameFilterString);
	    }
	    if (!StringUtils.isBlank(guestNameFilterString) && db.isEmpty()) {
		// filteredDates.add(db);
		it.remove();
	    }
	}
	// if (logger.isDebugEnabled()) {
	// logger.debug("Filtered dates: " + filteredDates.size());
	// }
    }

    @Override
    public boolean cleaningNeededFor(final String name, final LocalDate date) {

	return getData().cleaningNeededFor(name, date);
    }

    @Override
    public BooleanProperty cleaningsChangedProperty() {

	return getData().cleaningsChangedProperty();
    }

    public void clearData() {

	uiData.clear();
	uiDataMap.clear();
	data.clear();
    }

    @Override
    public BookingBean createAndAddBooking(final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) throws OverbookingException {
	return data.createAndAddBooking(checkInDate, checkOutDate, guestName, roomName, source);
    }

    @Override
    public BookingBean createBooking(final String bookingId, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String source) {

	return getData().createBooking(bookingId, checkInDate, checkOutDate, guestName, roomName, source);
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

    @Override
    public Optional<BookingEntryPair> getAfter(final BookingEntry e, final int numDays) {

	return data.getAfter(e, numDays);
    }

    @Override
    public Optional<BookingEntryPair> getBefore(final BookingEntry e, final int numDays) {

	return data.getBefore(e, numDays);
    }

    @Override
    public List<BookingEntry> getBookingEntries() {

	return getData().getBookingEntries();
    }

    @Override
    public Optional<BookingEntryPair> getBookingEntryPair(final String name, final LocalDate date) {

	return getData().getBookingEntryPair(name, date);
    }

    @Override
    public List<BookingEntryPair> getBookingEntryPairs() {

	return getData().getBookingEntryPairs();
    }

    @Override
    public List<BookingBean> getBookings() {

	return getData().getBookings();
    }

    @Override
    public List<CleaningEntry> getCleaningEntries() {

	return getData().getCleaningEntries();
    }

    @Override
    public Optional<CleaningEntry> getCleaningEntry(final String name, final LocalDate date) {

	return getData().getCleaningEntry(name, date);
    }

    DrBookingsDataImpl getData() {

	return data;
    }

    @Override
    public Optional<BookingEntry> getFirstBookingEntry(final Optional<BookingEntryPair> bookingsThatDay) {

	return data.getFirstBookingEntry(bookingsThatDay);
    }

    @Override
    public Optional<BookingEntry> getFirstBookingEntry(final String roomName, final LocalDate date) {

	return data.getFirstBookingEntry(roomName, date);
    }

    @Override
    public Optional<BookingEntry> getLastBookingEntry(final Optional<BookingEntryPair> bookingsThatDay) {

	return data.getLastBookingEntry(bookingsThatDay);
    }

    @Override
    public Optional<BookingEntry> getLastBookingEntry(final String roomName, final LocalDate date) {

	return data.getLastBookingEntry(roomName, date);
    }

    @Override
    public Optional<BookingEntryPair> getOneDayAfter(final BookingEntry e) {

	return data.getOneDayAfter(e);
    }

    @Override
    public Optional<BookingEntryPair> getOneDayBefore(final BookingEntry e) {

	return data.getOneDayBefore(e);
    }

    public synchronized ObservableList<DateBean> getUIData() {

	return uiData;
    }

    public void modifyBooking(final BookingBean booking, final LocalDate checkInDate, final LocalDate checkOutDate)
	    throws OverbookingException {

	removeBooking(booking);
	final BookingBean newBooking = new BookingBean(booking.getId(), booking.getGuest(), booking.getRoom(),
		booking.getBookingOrigin(), checkInDate, checkOutDate);
	newBooking.setCheckInNote(booking.getCheckInNote());
	newBooking.setCheckOutNote(booking.getCheckOutNote());
	newBooking.setExternalId(booking.getExternalId());
	newBooking.setGrossEarningsExpression(booking.getGrossEarningsExpression());
	newBooking.setPaymentDone(booking.isPaymentDone());
	newBooking.setWelcomeMailSend(booking.isWelcomeMailSend());
	newBooking.setSpecialRequestNote(booking.getSpecialRequestNote());
	newBooking.setCalendarIds(booking.getCalendarIds());
	// newBooking.setCleaning(booking.getCleaning());
	System.err.println("Removed cleaning");
	newBooking.setCleaningFees(booking.getCleaningFees());
	newBooking.setDateOfPayment(booking.getDateOfPayment());
	newBooking.setServiceFee(booking.getServiceFee());
	newBooking.setServiceFeesPercent(booking.getServiceFeesPercent());
	newBooking.setSplitBooking(booking.isSplitBooking());
	newBooking.setPayments(booking.getPayments());
	try {
	    data.addBooking(newBooking);
	} catch (final OverbookingException e) {
	    data.addBooking(booking);
	}
    }

    public synchronized void removeBooking(final BookingBean booking) {

	data.removeBooking(booking);
    }

    public synchronized void removeBookings(final List<BookingBean> bookings) {

	data.removeBookings(bookings);
    }

    public void removeCleaning(final CleaningEntry cleaningEntry) {

	data.removeCleaning(cleaningEntry);
    }

    @Override
    public void removeCleaning(final LocalDate date, final String roomName) {
	data.removeCleaning(date, roomName);

    }

    @Override
    public void setCleaning(final String name, final LocalDate date, final String roomName) {
	data.setCleaning(name, date, roomName);

    }
}

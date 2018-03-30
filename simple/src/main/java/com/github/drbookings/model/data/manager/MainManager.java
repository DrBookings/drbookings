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

package com.github.drbookings.model.data.manager;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.*;
import com.github.drbookings.model.settings.SettingsManager;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainManager {

    private static final Logger logger = LoggerFactory.getLogger(MainManager.class);

    private final Multimap<LocalDate, BookingEntry> bookingEntries;

    private final BookingOriginProvider bookingOriginProvider;

    private final List<BookingBean> bookings;

    private final Multimap<LocalDate, CleaningEntry> cleaningEntries;

    private final ListProperty<CleaningEntry> cleaningEntriesList = new SimpleListProperty<>(
            FXCollections.observableArrayList());

    private final CleaningProvider cleaningProvider;

    private final List<DateBean> filteredDates = new ArrayList<>();

    private final GuestProvider guestProvider;

    private final RoomProvider roomProvider;

    private final ListProperty<DateBean> uiData;

    private final Map<LocalDate, DateBean> uiDataMap;

    MainManager() {
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

    public static MainManager getInstance() {
        return InstanceHolder.instance;
    }

    public Optional<BookingEntry> getAfter(BookingEntry e) {
        LocalDate plusOneDay = e.getDate().plusDays(1);
        Room room = e.getRoom();
        Collection<BookingEntry> bookingsThatDay = bookingEntries.get(plusOneDay);
        if (bookingsThatDay != null) {
            return bookingsThatDay.stream().filter(b -> b.getRoom().equals(room)).findFirst();
        }
        return Optional.empty();
    }

    public Optional<BookingEntry> getBefore(BookingEntry e) {
        LocalDate plusOneDay = e.getDate().minusDays(1);
        Room room = e.getRoom();
        Collection<BookingEntry> bookingsThatDay = bookingEntries.get(plusOneDay);
        if (bookingsThatDay != null) {
            return bookingsThatDay.stream().filter(b -> b.getRoom().equals(room)).findFirst();
        }
        return Optional.empty();
    }

    public synchronized BookingBean addBooking(final BookingBean booking) throws OverbookingException {
        for (final BookingEntry b : Bookings.toEntries(booking)) {
            if (roomBusy(b)) {
                throw new OverbookingException("Cannot add " + booking);
            }
        }
        for (final BookingEntry b : Bookings.toEntries(booking)) {
            addBookingEntry(b);
        }
        // if (logger.isDebugEnabled()) {
        // logger.debug("Adding booking " + booking);
        // }
        bookings.add(booking);
        return booking;
    }

    protected synchronized void addBookingEntry(final BookingEntry bookingEntry) {
        bookingEntries.put(bookingEntry.getDate(), bookingEntry);
        addUiDataBooking(bookingEntry);
    }

    public synchronized CleaningEntry addCleaning(final LocalDate date, final String cleaningName,
                                                  final BookingBean booking) {
        Objects.requireNonNull(date, "Date must not be null");
        Objects.requireNonNull(booking, "BookingBean must not be null");
        if (cleaningName == null || cleaningName.trim().length() == 0) {
            throw new IllegalArgumentException();
        }

        final Cleaning cleaning = cleaningProvider.getOrCreateElement(cleaningName);
        final CleaningEntry cleaningEntry = new CleaningEntry(date, booking, cleaning, this);
        cleaningEntry.setCleaningCosts(SettingsManager.getInstance().getCleaningCosts());
        if (containsCleaningByNameDateRoom(cleaningEntry)) {
            if (logger.isWarnEnabled()) {
                logger.warn("Skip cleaning " + cleaning);
            }
        } else {
            cleaningEntries.put(date, cleaningEntry);
            cleaningEntriesList.add(cleaningEntry);
            addUiDataCleaning(cleaningEntry);

            return cleaningEntry;
        }
        return null;

    }

    public synchronized CleaningEntry addCleaning(final LocalDate date, final String cleaningName,
                                                  final String roomName) throws MatchException {
        return addCleaning(date, cleaningName, findBooking(date, roomName));
    }

    private BookingBean findBooking(final LocalDate date, final String roomName) throws MatchException {
        final int maxCount = 100;
        int count = 0;
        LocalDate date2 = date;
        Collection<BookingBean> result2 = null;
        do {
            result2 = bookingEntries.get(date2).stream().filter(b -> b.getRoom().getName().equals(roomName))
                    .filter(b -> !b.isCheckIn()).map(b -> b.getElement()).collect(Collectors.toSet());
            if (result2.stream().anyMatch(b -> b.getCleaning() != null)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Found entry with cleaning, aborting");
                }
                throw new MatchException("Failed to find matching booking for " + date + " and " + roomName);
            }
            result2 = result2.stream().filter(b -> b.getCleaning() == null).collect(Collectors.toSet());
            count++;
            date2 = date2.minusDays(1);
        } while ((result2 == null || result2.isEmpty()) && count < maxCount);
        if (count == maxCount) {
            throw new MatchException("Failed to find matching booking for " + date + " and " + roomName);
        }
        if (result2.size() > 1) {
            throw new MatchException("Found more than one matching booking");
        }
        return result2.iterator().next();

    }

    private void addDateBean(final DateBean db) {
        uiData.add(db);
        uiDataMap.put(db.getDate(), db);
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

    public synchronized void applyFilter(final String guestNameFilterString) {
        uiData.addAll(filteredDates);
        filteredDates.clear();
        for (final Iterator<DateBean> it = uiData.iterator(); it.hasNext(); ) {
            final DateBean db = it.next();
            for (final RoomBean rb : db.getRooms()) {
                rb.setBookingFilterString(guestNameFilterString);
            }
            if (!StringUtils.isBlank(guestNameFilterString) && db.isEmpty()) {
                filteredDates.add(db);
                it.remove();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("Filtered dates: " + filteredDates.size());
        }
    }

    public ListProperty<CleaningEntry> cleaningEntriesListProperty() {
        return this.cleaningEntriesList;
    }

    public void clearData() {
        bookingEntries.clear();
        cleaningEntries.clear();
        bookings.clear();
        cleaningEntriesList.clear();
        filteredDates.clear();
        uiData.clear();
        uiDataMap.clear();
    }

    public boolean containsBookingByNameAndDate(final BookingBean booking) {
        for (final BookingBean b : bookings) {
            if (b.getGuest().getName().equals(booking.getGuest().getName())
                    && b.getCheckIn().equals(booking.getCheckIn()) && b.getCheckOut().equals(booking.getCheckOut())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsCleaningByNameDateRoom(final CleaningEntry cleaning) {
        for (final Entry<LocalDate, CleaningEntry> b : cleaningEntries.entries()) {
            if (b.getValue().getElement().getName().equals(cleaning.getElement().getName())
                    && b.getKey().equals(cleaning.getDate()) && b.getValue().getRoom().equals(cleaning.getRoom())) {
                return true;
            }
        }
        return false;
    }

    public synchronized BookingBean createBooking(final LocalDate checkInDate, final LocalDate checkOutDate,
                                                  final String guestName, final String roomName, final String originName) {
        return createBooking(null, checkInDate, checkOutDate, guestName, roomName, originName);
    }

    /**
     * The booking will <b>not</b> be added.
     *
     * @param id
     * @param checkInDate
     * @param checkOutDate
     * @param guestName
     * @param roomName
     * @param originName
     * @return
     * @throws OverbookingException
     */
    public synchronized BookingBean createBooking(final String id, final LocalDate checkInDate,
                                                  final LocalDate checkOutDate, final String guestName, final String
                                                      roomName, final String originName) {
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
        final BookingBean booking = new BookingBean(id, guest, room, bookingOrigin, checkInDate, checkOutDate);
        return booking;
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

    public synchronized List<BookingEntry> getBookingEntries() {
        return Collections.unmodifiableList(new ArrayList<>(bookingEntries.values()));
    }

    public synchronized Collection<BookingEntry> getBookingEntries(final LocalDate date) {
        return Collections.unmodifiableCollection(bookingEntries.get(date));
    }

    public synchronized List<BookingBean> getBookings() {
        return bookings;
    }

    public synchronized Collection<CleaningEntry> getCleaningEntries() {
        return Collections.unmodifiableCollection(cleaningEntries.values());
    }

    public List<CleaningEntry> getCleaningEntriesList() {
        return this.cleaningEntriesListProperty().get();
    }

    public void setCleaningEntriesList(final Collection<? extends CleaningEntry> cleaningEntriesList) {
        this.cleaningEntriesListProperty().setAll(cleaningEntriesList);
    }

    public synchronized ObservableList<DateBean> getUIData() {
        return uiData;
    }

    public boolean hasCheckIn(final LocalDate date, final String roomName) {
        final Collection<BookingEntry> be = bookingEntries.get(date);
        return be.stream().anyMatch(b -> b.isCheckIn() && b.getRoom().getName().equals(roomName));
    }

    // public synchronized boolean needsCleaning(final String roomName, final
    // LocalDate date) {
    // final List<LocalDate> dates = new
    // ArrayList<>(cleaningEntries.asMap().keySet());
    // if (!dates.isEmpty()) {
    // Collections.sort(dates, Comparator.reverseOrder());
    // final LocalDate lastDate = dates.get(0);
    // if (lastDate.isBefore(date)) {
    // return true;
    // }
    // for (final LocalDate d : new DateRange(date, lastDate)) {
    //
    // if (!date.equals(d)) {
    // final Collection<BookingEntry> bookings = bookingEntries.get(d).stream()
    // .filter(b ->
    // b.getRoom().getName().equals(roomName)).collect(Collectors.toList());
    // if (!bookings.isEmpty()) {
    // // another booking in between
    // return true;
    // }
    // }
    //
    // final Collection<CleaningEntry> e = cleaningEntries.get(d);
    // if (e != null && !e.isEmpty() &&
    // CleaningEntry.roomNameView(e).contains(roomName)) {
    // return false;
    // }
    // }
    // }
    // return true;
    // }

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
        newBooking.setCleaning(booking.getCleaning());
        newBooking.setCleaningFees(booking.getCleaningFees());
        newBooking.setDateOfPayment(booking.getDateOfPayment());
        newBooking.setServiceFee(booking.getServiceFee());
        newBooking.setServiceFeesPercent(booking.getServiceFeesPercent());
        newBooking.setSplitBooking(booking.isSplitBooking());
        try {
            addBooking(newBooking);
        } catch (final OverbookingException e) {
            addBooking(booking);
        }

    }

    public synchronized boolean needsCleaning(final String roomName, final LocalDate date) {
        final Collection<BookingEntry> bookings = bookingEntries.get(date);
        final Stream<BookingEntry> sbe = bookings.stream().filter(be -> be.getRoom().getName().equals(roomName));
        final Stream<BookingBean> sb = sbe.map(be -> be.getElement());
        return sb.anyMatch(b -> b.getCleaning() == null);
    }

    public synchronized boolean removeBooking(final BookingBean booking) {
        return removeBookings(Arrays.asList(booking));
    }

    public synchronized boolean removeBookings(final List<BookingBean> bookings) {
        this.bookings.removeAll(bookings);
        for (final Iterator<BookingEntry> it = bookingEntries.values().iterator(); it.hasNext(); ) {
            final BookingEntry be = it.next();
            if (bookings.contains(be.getElement())) {
                it.remove();
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("BookingBean entries now " + bookingEntries.size());
        }
        final boolean result = this.bookings.removeAll(bookings);
        if (logger.isDebugEnabled()) {
            logger.debug("Bookings now " + this.bookings.size());
        }
        removeUiDataBooking(bookings);
        return result;
    }

    public void removeCleaning(final CleaningEntry cleaningEntry) {
        cleaningEntries.values().remove(cleaningEntry);
        cleaningEntriesList.remove(cleaningEntry);
        bookings.forEach(b -> {
            if (cleaningEntry.equals(b.getCleaning())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Removing cleaning from booking " + b);
                }
                b.setCleaning(null);

            }
        });
        removeUiDataCleaning(cleaningEntry);
    }

    private void removeUiDataBooking(final Collection<? extends BookingBean> bookings) {
        for (final DateBean e : uiData) {
            for (final RoomBean r : e.getRooms()) {
                for (final Iterator<BookingEntry> it = r.getBookingEntries().iterator(); it.hasNext(); ) {
                    final BookingEntry be = it.next();
                    for (final BookingBean bb : bookings) {
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

    private boolean roomBusy(final BookingEntry bookingEntry) {
        if (!bookingEntry.isCheckOut()) {
            final Room room = bookingEntry.getElement().getRoom();
            final LocalDate date = bookingEntry.getDate();
            final Collection<BookingEntry> b = bookingEntries.get(date);
            if (b != null) {
                for (final BookingEntry be : b) {
                    if (be.isCheckOut()) {
                        continue;
                    }
                    final Room room2 = be.getElement().getRoom();
                    if (room2.equals(room)) {
                        if (logger.isWarnEnabled()) {
                            logger.warn("Room " + room + " busy with " + be);
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public Optional<BookingBean> getBooking(final String bookingId) {
        final Collection<BookingBean> c = bookings.stream().filter(b -> b.getId().equals(bookingId))
                .collect(Collectors.toSet());
        if (!c.isEmpty()) {
            if (c.size() > 1) {
                throw new RuntimeException("Ambiguous booking id " + bookingId);
            }
            return Optional.of(c.iterator().next());
        }
        return Optional.empty();
    }

    private static class InstanceHolder {
        private static final MainManager instance = new MainManager();
    }

}

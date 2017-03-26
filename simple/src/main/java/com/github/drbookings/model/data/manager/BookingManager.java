package com.github.drbookings.model.data.manager;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.manager.BookingOriginManager;
import com.github.drbookings.model.manager.GuestManager;
import com.github.drbookings.model.manager.RoomManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookingManager {

    public BookingOriginManager getBookingOriginManager() {
	return bookingOriginManager;
    }

    public void setBookingOriginManager(final BookingOriginManager bookingOriginManager) {
	this.bookingOriginManager = bookingOriginManager;
    }

    private final Multimap<Guest, Booking> guestToBookingsMap = ArrayListMultimap.create();

    private final Multimap<LocalDate, Booking> dateToBookingsMap = ArrayListMultimap.create();

    private final ObservableList<DateBean> uiData = FXCollections.observableArrayList(DateBean.extractor());

    public ObservableList<DateBean> getUIData() {
	return uiData;
    }

    private RoomManager roomManager = new RoomManager();

    private GuestManager guestManager = new GuestManager();

    private BookingOriginManager bookingOriginManager = new BookingOriginManager();

    public synchronized void addBooking(final Booking booking) {
	Objects.requireNonNull(booking);
	guestToBookingsMap.put(booking.getGuest(), booking);
	for (final LocalDate date : new DateRange(booking.getCheckIn(), booking.getCheckOut())) {
	    dateToBookingsMap.put(date, booking);
	}
    }

    public synchronized Booking addBooking(final String guestName, final String roomName, final LocalDate checkIn,
	    final LocalDate checkOut) throws OverbookingException {
	final Guest guest = guestManager.getOrCreateElement(guestName);
	final Room room = roomManager.getOrCreateElement(roomName);
	final Booking booking = new Booking(guest, room, checkIn, checkOut);
	addBooking(booking);
	return booking;
    }

    public synchronized Collection<Booking> getBookings() {
	return Collections.unmodifiableSet(new LinkedHashSet<Booking>(guestToBookingsMap.values()));
    }

    public GuestManager getGuestManager() {
	return guestManager;
    }

    public RoomManager getRoomManager() {
	return roomManager;
    }

    public void setGuestManager(final GuestManager guestManager) {
	this.guestManager = guestManager;
    }

    public void setRoomManager(final RoomManager roomManager) {
	this.roomManager = roomManager;
    }

}

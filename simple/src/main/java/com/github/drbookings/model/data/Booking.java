package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;

import com.github.drbookings.DateRange;
import com.github.drbookings.model.bean.BookingBean;

public class Booking extends IDed implements Iterable<BookingBean> {

    private String externalId;

    private final Collection<BookingBean> bookingEntries = new ArrayList<>();

    private final LocalDate checkIn;

    private final LocalDate checkOut;

    private final Guest guest;

    private final Room room;

    private BookingOrigin bookingOrigin;

    public Booking(final Guest guest, final Room room, final LocalDate checkIn, final LocalDate checkOut) {
	Objects.requireNonNull(guest);
	Objects.requireNonNull(room);
	Objects.requireNonNull(checkIn);
	Objects.requireNonNull(checkOut);

	this.checkIn = checkIn;
	this.checkOut = checkOut;
	this.guest = guest;
	this.room = room;

	fillEntries();
    }

    private void fillEntries() {
	for (final LocalDate date : new DateRange(checkIn, checkOut)) {
	    bookingEntries.add(BookingBean.create(getGuest().getName(), getRoom().getName(), date));
	}
    }

    public Collection<BookingBean> getBookingEntries() {
	return Collections.unmodifiableCollection(bookingEntries);
    }

    public BookingOrigin getBookingOrigin() {
	return bookingOrigin;
    }

    public LocalDate getCheckIn() {
	return checkIn;
    }

    public LocalDate getCheckOut() {
	return checkOut;
    }

    public String getExternalId() {
	return externalId;
    }

    public Guest getGuest() {
	return guest;
    }

    public Room getRoom() {
	return room;
    }

    @Override
    public Iterator<BookingBean> iterator() {
	return getBookingEntries().iterator();
    }

    public void setBookingOrigin(final BookingOrigin bookingOrigin) {
	this.bookingOrigin = bookingOrigin;
    }

    public void setExternalId(final String externalId) {
	this.externalId = externalId;
    }

}

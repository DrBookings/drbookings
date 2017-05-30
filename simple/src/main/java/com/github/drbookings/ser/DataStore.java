package com.github.drbookings.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.model.ser.CleaningBeanSer;
import com.github.drbookings.ui.CleaningEntry;

@XmlRootElement
public class DataStore {

    private static final Logger logger = LoggerFactory.getLogger(DataStore.class);

    public static CleaningBeanSer transform(final CleaningEntry c) {
	final CleaningBeanSer b = new CleaningBeanSer();
	b.date = c.getDate();
	b.name = c.getElement().getName();
	b.room = c.getRoom().getName();
	b.calendarIds = c.getCalendarIds();
	return b;
    }

    public static BookingBeanSer transform(final Booking bb) {

	final BookingBeanSer result = new BookingBeanSer();
	result.checkInDate = bb.getCheckIn();
	result.checkOutDate = bb.getCheckOut();
	result.bookingId = bb.getId();
	// result.grossEarnings = bb.getGrossEarnings();
	result.grossEarningsExpression = bb.getGrossEarningsExpression();
	result.guestName = bb.getGuest().getName();
	result.roomName = bb.getRoom().getName();
	result.source = bb.getBookingOrigin().getName();
	result.welcomeMailSend = bb.isWelcomeMailSend();
	result.serviceFee = bb.getServiceFee();
	result.checkInNote = bb.getCheckInNote();
	result.paymentDone = bb.isPaymentDone();
	result.specialRequestNote = bb.getSpecialRequestNote();
	result.checkOutNote = bb.getCheckOutNote();
	result.calendarIds = bb.getCalendarIds();

	return result;
    }

    public DataStore() {

    }

    public DataStore setBookingSer(final Collection<? extends BookingBeanSer> bookings) {
	this.bookings.clear();
	this.bookings.addAll(bookings);
	return this;
    }

    @XmlElementWrapper(name = "bookings")
    @XmlElement(name = "booking")
    public List<BookingBeanSer> getBookingsSer() {
	return bookings;
    }

    @XmlElementWrapper(name = "cleanings")
    @XmlElement(name = "cleaning")
    public List<CleaningBeanSer> getCleaningsSer() {
	return cleanings;
    }

    private final List<BookingBeanSer> bookings = new ArrayList<>();

    private final List<CleaningBeanSer> cleanings = new ArrayList<>();

    public void load(final MainManager manager) throws OverbookingException {
	final List<Booking> bookingsToAdd = new ArrayList<>();
	for (final BookingBeanSer bb : (Iterable<BookingBeanSer>) () -> getBookingsSer().stream()
		.sorted((b1, b2) -> b1.checkInDate.compareTo(b2.checkInDate)).iterator()) {
	    try {
		final Booking b = manager.createBooking(bb.bookingId, bb.checkInDate, bb.checkOutDate, bb.guestName,
			bb.roomName, bb.source);
		// b.setGrossEarnings(bb.grossEarnings);
		b.setGrossEarningsExpression(bb.grossEarningsExpression);
		b.setWelcomeMailSend(bb.welcomeMailSend);
		b.setCheckInNote(bb.checkInNote);
		b.setPaymentDone(bb.paymentDone);
		b.setSpecialRequestNote(bb.specialRequestNote);
		b.setCheckOutNote(bb.checkOutNote);
		b.setExternalId(bb.externalId);
		b.setCalendarIds(bb.calendarIds);
		bookingsToAdd.add(b);
	    } catch (final Exception e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	}

	bookingsToAdd.forEach(b -> {
	    try {
		manager.addBooking(b);
	    } catch (final OverbookingException e) {
		if (logger.isWarnEnabled()) {
		    logger.warn(e.getLocalizedMessage());
		}
	    }
	});

	for (final CleaningBeanSer cb : getCleaningsSer()) {
	    manager.addCleaning(cb.date, cb.name, cb.room).setCalendarIds(cb.calendarIds);
	    ;
	}
    }
}

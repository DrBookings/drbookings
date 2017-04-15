package com.github.drbookings.ser;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.model.ser.CleaningBeanSer;
import com.github.drbookings.ui.controller.CleaningEntry;

@XmlRootElement
public class DataStore {

    public static CleaningBeanSer transform(final CleaningEntry c) {
	final CleaningBeanSer b = new CleaningBeanSer();
	b.date = c.getDate();
	b.name = c.getElement().getName();
	b.room = c.getRoom().getName();
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

	return result;
    }

    public DataStore() {

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
	for (final BookingBeanSer bb : (Iterable<BookingBeanSer>) () -> getBookingsSer().stream()
		.sorted((b1, b2) -> b1.checkInDate.compareTo(b2.checkInDate)).iterator()) {
	    final Booking b = manager.addBooking(bb.bookingId, bb.checkInDate, bb.checkOutDate, bb.guestName,
		    bb.roomName, bb.source);
	    // b.setGrossEarnings(bb.grossEarnings);
	    b.setGrossEarningsExpression(bb.grossEarningsExpression);
	    b.setWelcomeMailSend(bb.welcomeMailSend);
	    b.setCheckInNote(bb.checkInNote);
	    b.setPaymentDone(bb.paymentDone);
	}
	for (final CleaningBeanSer cb : getCleaningsSer()) {
	    manager.addCleaning(cb.date, cb.name, cb.room);
	}
    }
}

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

package com.github.drbookings.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.Payment;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingBeanFactory;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.model.exception.OverbookingException;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.model.ser.CleaningBeanSer;
import com.github.drbookings.model.ser.PaymentSer;
import com.github.drbookings.ui.CleaningEntry;

@XmlRootElement
public class DataStore {

    private static final Logger logger = LoggerFactory.getLogger(DataStore.class);

    public static BookingBeanSer transform(final BookingBean bb) {

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
	result.serviceFeePercent = bb.getServiceFeesPercent();
	result.cleaningFees = bb.getCleaningFees();
	result.checkInNote = bb.getCheckInNote();
	result.paymentDone = bb.isPaymentDone();
	result.specialRequestNote = bb.getSpecialRequestNote();
	result.checkOutNote = bb.getCheckOutNote();
	result.calendarIds = bb.getCalendarIds();
	result.dateOfPayment = bb.getDateOfPayment();
	result.splitBooking = bb.isSplitBooking();
	result.paymentsSoFar = PaymentSer.transform(bb.getPayments());
	return result;
    }

    public static CleaningBeanSer transform(final CleaningEntry c) {
	final CleaningBeanSer b = new CleaningBeanSer();
	b.date = c.getDate();
	b.name = c.getElement().getName();
	b.room = c.getRoom().getName();
	b.calendarIds = c.getCalendarIds();
	b.cleaningCosts = c.getCleaningCosts();
	// if (c.getBooking() != null) {
	// b.bookingId = c.getBooking().getId();
	// }
	// System.err.println("Removed cleaning");
	return b;
    }

    public static List<BookingBean> transform(final Collection<? extends BookingBeanSer> sers) {
	final List<BookingBean> bookingsToAdd = new ArrayList<>();
	for (final BookingBeanSer bb : sers) {
	    try {
		final BookingBean b = new BookingBeanFactory().createBooking(bb.bookingId, bb.checkInDate,
			bb.checkOutDate, bb.guestName, bb.roomName, bb.source);
		// b.setGrossEarnings(bb.grossEarnings);
		b.setGrossEarningsExpression(bb.grossEarningsExpression);
		b.setWelcomeMailSend(bb.welcomeMailSend);
		b.setCheckInNote(bb.checkInNote);
		b.setPaymentDone(bb.paymentDone);
		b.setSpecialRequestNote(bb.specialRequestNote);
		b.setCheckOutNote(bb.checkOutNote);
		b.setExternalId(bb.externalId);
		b.setCalendarIds(bb.calendarIds);
		b.setCleaningFees(bb.cleaningFees);
		b.setServiceFeesPercent(bb.serviceFeePercent);
		b.setDateOfPayment(bb.dateOfPayment);
		b.setSplitBooking(bb.splitBooking);
		b.setPayments(Payment.transform(bb.paymentsSoFar));
		bookingsToAdd.add(b);
	    } catch (final Exception e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	}
	return bookingsToAdd;
    }

    private final List<BookingBeanSer> bookings = new ArrayList<>();

    private final List<CleaningBeanSer> cleanings = new ArrayList<>();

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

    public void load(final MainManager manager) {
	final List<BookingBean> bookingsToAdd = new ArrayList<>();
	for (final BookingBeanSer bb : (Iterable<BookingBeanSer>) () -> getBookingsSer().stream()
		.sorted((b1, b2) -> b1.checkInDate.compareTo(b2.checkInDate)).iterator()) {
	    try {
		final BookingBean b = manager.createBooking(bb.bookingId, bb.checkInDate, bb.checkOutDate, bb.guestName,
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
		b.setCleaningFees(bb.cleaningFees);
		b.setServiceFeesPercent(bb.serviceFeePercent);
		b.setDateOfPayment(bb.dateOfPayment);
		b.setSplitBooking(bb.splitBooking);
		b.setPayments(Payment.transform(bb.paymentsSoFar));
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
	if (logger.isDebugEnabled()) {
	    logger.debug(bookingsToAdd.size() + " added");
	}

	for (final CleaningBeanSer cb : getCleaningsSer()) {
	    try {
		final CleaningEntry ce = manager.addCleaning(cb.name, cb.date, cb.room);
		ce.setCalendarIds(cb.calendarIds);
		ce.setCleaningCosts(cb.cleaningCosts);
	    } catch (final AlreadyBusyException e) {
		e.printStackTrace();
	    }

	    // if (b.isPresent()) {
	    // ce.bookingProperty().set(b.get());
	    // } else {
	    // if (logger.isWarnEnabled()) {
	    // logger.warn("Cannot link " + ce + " to booking, failed to find booking for ID
	    // " + cb.bookingId);
	    // }
	    // }
	    // System.err.println("Removed cleaning");
	}
    }

    public DataStore setBookingSer(final Collection<? extends BookingBeanSer> bookings) {
	this.bookings.clear();
	this.bookings.addAll(bookings);
	return this;
    }
}

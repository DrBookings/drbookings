package com.github.drbookings.ser;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
		b.cleaningCosts = c.getCleaningCosts();
		if (c.getBooking() != null) {
			b.bookingId = c.getBooking().getId();
		}
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
		result.serviceFeePercent = bb.getServiceFeesPercent();
		result.cleaningFees = bb.getCleaningFees();
		result.checkInNote = bb.getCheckInNote();
		result.paymentDone = bb.isPaymentDone();
		result.specialRequestNote = bb.getSpecialRequestNote();
		result.checkOutNote = bb.getCheckOutNote();
		result.calendarIds = bb.getCalendarIds();
		result.dateOfPayment = bb.getDateOfPayment();
		result.splitBooking = bb.isSplitBooking();
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
				b.setCleaningFees(bb.cleaningFees);
				b.setServiceFeesPercent(bb.serviceFeePercent);
				b.setDateOfPayment(bb.dateOfPayment);
				b.setSplitBooking(bb.splitBooking);
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

			final Optional<Booking> b = manager.getBooking(cb.bookingId);
			if (b.isPresent()) {
				manager.addCleaning(cb.date, cb.name, b.get()).setCalendarIds(cb.calendarIds)
						.setCleaningCosts(cb.cleaningCosts);
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("Failed to add cleaning " + cb + ", failed to find booking for ID " + cb.bookingId);
				}
			}

		}
	}
}

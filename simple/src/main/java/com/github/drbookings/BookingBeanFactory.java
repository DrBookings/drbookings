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
package com.github.drbookings;

import com.github.drbookings.ser.BookingBeanSer;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BookingBeanFactory {

    /**
     * Use provider to keep name unique.
     */
    private final GuestProvider guestProvider;
    /**
     * Use provider to keep name unique.
     */
    private final RoomFactory roomProvider;
    /**
     * Use provider to keep name unique.
     */
    private final BookingOriginProvider bookingOriginProvider;

    public BookingBeanFactory() {

	this(new GuestProvider(), RoomFactory.getInstance(), new BookingOriginProvider());
    }

    public BookingBeanFactory(final GuestProvider guestProvider, final RoomFactory roomProvider,
	    final BookingOriginProvider bookingOriginProvider) {

	this.guestProvider = guestProvider;
	this.roomProvider = roomProvider;
	this.bookingOriginProvider = bookingOriginProvider;
    }

    /**
     * @return the newly created {@link BookingBean}
     */
    public BookingBean createBooking(final String id, final LocalDate checkInDate, final LocalDate checkOutDate,
	    final String guestName, final String roomName, final String originName) {

	Objects.requireNonNull(checkInDate);
	Objects.requireNonNull(checkOutDate);
	if (StringUtils.isBlank(guestName))
	    throw new IllegalArgumentException("No guest name given");
	if (StringUtils.isBlank(roomName))
	    throw new IllegalArgumentException("No room name given");
	final Guest guest = guestProvider.getOrCreateElement(guestName);
	final Room room = roomProvider.getOrCreateElement(roomName);
	final BookingOrigin bookingOrigin = bookingOriginProvider.getOrCreateElement(originName);
	final BookingBean booking = new BookingBean(id, guest, room, bookingOrigin, checkInDate, checkOutDate);
	return booking;
    }

    public List<BookingBean> build(final Collection<? extends BookingBeanSer> elements) {

	return elements.stream().map(e -> createBooking(e)).collect(Collectors.toList());
    }

    public BookingBean createBooking(final BookingBeanSer e) {

	final BookingBean result = createBooking(e.bookingId, e.checkInDate, e.checkOutDate, e.guestName, e.roomName,
		e.source);
	result.setCalendarIds(e.calendarIds);
	result.setCheckInNote(e.checkInNote);
	result.setCheckOutNote(e.checkOutNote);
	result.setSpecialRequestNote(e.specialRequestNote);
	result.setCleaningFees(Payments.createMondary(e.cleaningFees).getNumber().floatValue());
	result.setDateOfPayment(e.dateOfPayment);
	result.setExternalId(e.externalId);
	result.setGrossEarningsExpression(e.grossEarningsExpression);
	result.setPaymentDone(e.paymentDone);
	result.setPayments(PaymentImpl.build(e.paymentsSoFar));
	result.setServiceFee(e.serviceFee);
	result.setServiceFeesPercent(e.serviceFeePercent);
	result.setSplitBooking(e.splitBooking);
	result.setWelcomeMailSend(e.welcomeMailSend);
	return result;
    }
}

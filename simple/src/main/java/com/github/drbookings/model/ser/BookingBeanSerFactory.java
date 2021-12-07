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

package com.github.drbookings.model.ser;

import com.github.drbookings.BookingBean;
import com.github.drbookings.ser.BookingBeanSer;
import com.github.drbookings.ser.PaymentSerFactory;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookingBeanSerFactory {

    public static List<BookingBeanSer> build(final Collection<? extends BookingBean> bookings) {
	return bookings.stream().map(e -> build(e)).collect(Collectors.toList());
    }

    public static BookingBeanSer build(final BookingBean bb) {

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
	result.cleaningFees = Float.toString(bb.getCleaningFees());
	result.checkInNote = bb.getCheckInNote();
	result.paymentDone = bb.isPaymentDone();
	result.specialRequestNote = bb.getSpecialRequestNote();
	result.checkOutNote = bb.getCheckOutNote();
	result.calendarIds = bb.getCalendarIds();
	result.dateOfPayment = bb.getDateOfPayment();
	result.splitBooking = bb.isSplitBooking();
	result.paymentsSoFar = PaymentSerFactory.build(bb.getPayments());
	return result;
    }

}

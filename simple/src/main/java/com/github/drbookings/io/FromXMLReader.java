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

package com.github.drbookings.io;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingBeanFactory;
import com.github.drbookings.PaymentImpl;
import com.github.drbookings.Payments;
import com.github.drbookings.ser.BookingBeanSer;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.MarshallListener;

public class FromXMLReader {

    private static final Logger logger = LoggerFactory.getLogger(FromXMLReader.class);
    private Unmarshaller.Listener listener;

    public Unmarshaller.Listener getListener() {

	return listener;
    }

    public static List<BookingBean> transform(final Collection<? extends BookingBeanSer> sers) {

	final List<BookingBean> bookingsToAdd = new ArrayList<>();
	for (final BookingBeanSer bb : sers) {
	    final BookingBean bbb = transform(bb);
	    if (bbb != null) {
		bookingsToAdd.add(bbb);
	    }
	}
	return bookingsToAdd;
    }

    public static BookingBean transform(final BookingBeanSer bb) {

	try {
	    final BookingBean b = new BookingBeanFactory().createBooking(bb.bookingId, bb.checkInDate, bb.checkOutDate,
		    bb.guestName, bb.roomName, bb.source);
	    // b.setGrossEarnings(bb.grossEarnings);
	    b.setGrossEarningsExpression(bb.grossEarningsExpression);
	    b.setWelcomeMailSend(bb.welcomeMailSend);
	    b.setCheckInNote(bb.checkInNote);
	    b.setPaymentDone(bb.paymentDone);
	    b.setSpecialRequestNote(bb.specialRequestNote);
	    b.setCheckOutNote(bb.checkOutNote);
	    b.setExternalId(bb.externalId);
	    b.setCalendarIds(bb.calendarIds);
	    b.setCleaningFees(Payments.createMondary(bb.cleaningFees).getNumber().floatValue());
	    b.setServiceFeesPercent(bb.serviceFeePercent);
	    b.setDateOfPayment(bb.dateOfPayment);
	    b.setSplitBooking(bb.splitBooking);
	    b.setPayments(PaymentImpl.build(bb.paymentsSoFar));
	    return b;
	} catch (final Exception e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
	return null;
    }

    public DataStoreCoreSer readFromFile(final Path file) throws JAXBException {

	return readFromFile(file.toFile());
    }

    public DataStoreCoreSer readFromFile(final File file) throws JAXBException {

	final JAXBContext jc = JAXBContext.newInstance(DataStoreCoreSer.class);
	final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();
	if (listener != null) {
	    jaxbMarshaller.setListener(listener);
	}
	final DataStoreCoreSer ds = (DataStoreCoreSer) jaxbMarshaller.unmarshal(file);
	if (logger.isDebugEnabled()) {
	    logger.debug("Loaded data from " + file);
	}
	return ds;
    }

    public FromXMLReader setListener(final Unmarshaller.Listener listener) {

	this.listener = listener;
	return this;
    }

    public void writeToFile(final DataStoreCoreSer ds, final File file) throws Exception {

	final JAXBContext jc = JAXBContext.newInstance(DataStoreCoreSer.class);
	final Marshaller jaxbMarshaller = jc.createMarshaller();
	jaxbMarshaller.setListener(new MarshallListener());
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.marshal(ds, file);
	if (logger.isInfoEnabled()) {
	    logger.info("Wrote to " + file);
	}
    }
}

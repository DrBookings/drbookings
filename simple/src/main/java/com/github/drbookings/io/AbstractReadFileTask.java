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

import com.github.BookingFactory;
import com.github.drbookings.Booking;
import com.github.drbookings.CleaningBeanSer2;
import com.github.drbookings.CleaningBeanSer2Factory;
import com.github.drbookings.RoomFactory;
import com.github.drbookings.ser.BookingBeanSer;
import com.github.drbookings.ser.CleaningBeanSer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.nio.file.Path;

public abstract class AbstractReadFileTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(AbstractReadFileTask.class);

    private final Path file;

    private final BookingFactory bookingFactory;

    public AbstractReadFileTask(final Path file) {
	this.file = file;
	bookingFactory = new BookingFactory(RoomFactory.getInstance());
    }

    @Override
    public void run() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Reading '" + file + "'");
	}
	try {
	    new FromXMLReader().setListener(new Unmarshaller.Listener() {
		@Override
		public void afterUnmarshal(final Object target, final Object parent) {

		    super.afterUnmarshal(target, parent);
		    if (target instanceof BookingBeanSer) {
			readBooking((BookingBeanSer) target);
		    } else if (target instanceof CleaningBeanSer2) {
			readCleaning((CleaningBeanSer2) target);
		    } else if (target instanceof CleaningBeanSer) {
			readCleaning(new CleaningBeanSer2Factory().build((CleaningBeanSer) target));
		    } else {
			if (logger.isWarnEnabled()) {
			    logger.warn("Unsupported type " + target);
			}
		    }
		}

	    }).readFromFile(file);
	} catch (final JAXBException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
    }

    protected void readCleaning(final CleaningBeanSer2 c) {
	handleNewCleaning(c);
    }

    protected abstract void handleNewCleaning(CleaningBeanSer2 c);

    protected void readBooking(final BookingBeanSer newBooking) {
	final Booking bbb = bookingFactory.build(newBooking);
	handleNewBooking(bbb);
    }

    protected abstract void handleNewBooking(Booking bbb);
}

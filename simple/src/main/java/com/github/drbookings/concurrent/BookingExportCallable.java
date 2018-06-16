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

package com.github.drbookings.concurrent;

import java.io.File;
import java.util.Collection;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.ser.XMLStorage;

public class BookingExportCallable implements Callable<Void> {

    private static final Logger logger = LoggerFactory.getLogger(BookingExportCallable.class);

    private final Collection<? extends BookingBean> bookings;

    public BookingExportCallable(final Collection<? extends BookingBean> bookings) {
	this.bookings = bookings;
    }

    @Override
    public Void call() throws Exception {

	if (logger.isInfoEnabled()) {
	    logger.info("Exporting " + bookings.size() + " bookings");
	}

	XMLStorage.save(XMLStorage.buildDataStore(bookings),
		new File(System.getProperty("user.home"), "booking-export.xml"));

	return null;
    }
}

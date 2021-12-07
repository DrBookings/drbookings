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

package com.github.drbookings.ui;

import com.github.drbookings.BookingFactory;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ser.BookingBeanSer;
import com.github.drbookings.ser.DataStore;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class BookingReaderService extends Service<Collection<BookingBeanSer>> {

    private final static Logger logger = LoggerFactory.getLogger(BookingReaderService.class);

    private final BookingFactory factory;

    public BookingReaderService(final MainManager manager, final BookingFactory factory) {
	super();
	this.factory = factory;
	setOnFailed(ee -> {
	    final Throwable e = getException();
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	    FXUIUtils.showError(e);
	});
	setOnSucceeded(e -> {
	    if (logger.isDebugEnabled()) {
		logger.debug("read " + getValue().size() + " bookings");
		try {
		    new DataStore().setBookingSer(getValue()).load(manager);
		} catch (final Exception e1) {
		    if (logger.isErrorEnabled()) {
			logger.error(e1.getLocalizedMessage(), e);
		    }
		    FXUIUtils.showError(e1);
		}
	    }
	});
    }

    @Override
    protected Task<Collection<BookingBeanSer>> createTask() {
	return new Task<Collection<BookingBeanSer>>() {

	    @Override
	    protected Collection<BookingBeanSer> call() throws Exception {
		return factory.build();
	    }
	};
    }

}

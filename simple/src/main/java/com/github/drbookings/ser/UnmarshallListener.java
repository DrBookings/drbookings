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

import javax.xml.bind.Unmarshaller.Listener;

import com.github.drbookings.model.ser.BookingBeanSer;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class UnmarshallListener extends Listener {

    // private static final Logger logger =
    // LoggerFactory.getLogger(UnmarshallListener.class);

    private final LongProperty bookingCount = new SimpleLongProperty(0);

    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
	super.afterUnmarshal(target, parent);
	if (target instanceof BookingBeanSer) {
	    Platform.runLater(() -> incrementBookingCount());
	}
    }

    @Override
    public void beforeUnmarshal(final Object target, final Object parent) {
	super.beforeUnmarshal(target, parent);

    }

    public synchronized LongProperty bookingCountProperty() {
	return this.bookingCount;
    }

    public synchronized long getBookingCount() {
	return this.bookingCountProperty().get();
    }

    private synchronized void incrementBookingCount() {
	setBookingCount(getBookingCount() + 1);
    }

    private synchronized void setBookingCount(final long bookingCount) {
	this.bookingCountProperty().set(bookingCount);

    }

}

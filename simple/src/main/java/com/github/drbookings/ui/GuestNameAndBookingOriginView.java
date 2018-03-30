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

import com.github.drbookings.model.BookingEntry;

import java.util.Collection;
import java.util.Iterator;

public class GuestNameAndBookingOriginView extends BookingEntryView {

    public GuestNameAndBookingOriginView(final Collection<BookingEntry> bookingEntries) {
	super(bookingEntries);
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	final Iterator<BookingEntry> it = bookingEntries.iterator();
	while (it.hasNext()) {
	    final BookingEntry b = it.next();
	    sb.append(b.getElement().getGuest().getName());
	    sb.append(" (");
	    sb.append(b.getElement().getBookingOrigin().getName());
	    sb.append(")");

	    if (it.hasNext()) {
		sb.append("\n");
	    }
	}

	return sb.toString();
    }

    public boolean isEmpty() {
	return bookingEntries.isEmpty();
    }

}

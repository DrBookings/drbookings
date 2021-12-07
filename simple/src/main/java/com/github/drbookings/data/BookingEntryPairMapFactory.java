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

package com.github.drbookings.data;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingEntry;
import com.github.drbookings.BookingEntryPair;
import com.github.drbookings.Bookings;
import com.github.drbookings.exception.OverbookingException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.TreeMap;

@Deprecated
public class BookingEntryPairMapFactory {

    public static TreeMap<LocalDate, BookingEntryPair> build(final Collection<? extends BookingBean> bookings) {
	final TreeMap<LocalDate, BookingEntryPair> result = new TreeMap<>();
	for (final BookingBean b : bookings) {
	    for (final BookingEntry e : Bookings.toEntries(b)) {
		BookingEntryPair pair = result.get(e.getDate());
		if (pair == null) {
		    pair = new BookingEntryPair(e.getDate());
		    result.put(e.getDate(), pair);
		}
		try {
		    pair.addBooking(e);
		} catch (final OverbookingException e1) {
		    throw new IllegalArgumentException(e1);
		}
	    }
	}
	return result;
    }

}

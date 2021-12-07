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

package com.github.drbookings.model.data;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingEntry;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;

public class BookingMapFactory {

    public static Multimap<BookingBean, BookingEntry> buildMap(final Collection<? extends BookingEntry> entries) {

	final Multimap<BookingBean, BookingEntry> result = ArrayListMultimap.create();

	for (final BookingEntry be : entries) {
	    result.put(be.getElement(), be);
	}

	return result;
    }
}

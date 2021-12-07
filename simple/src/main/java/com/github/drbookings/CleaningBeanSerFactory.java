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

import com.github.drbookings.ser.CleaningBeanSer;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CleaningBeanSerFactory {

    public static List<CleaningBeanSer> build(final Collection<? extends CleaningEntry> entries) {
	return entries.stream().map(e -> build(e)).collect(Collectors.toList());
    }

    public static CleaningBeanSer build(final CleaningEntry c) {
	final CleaningBeanSer b = new CleaningBeanSer();
	b.date = c.getDate();
	b.name = c.getElement().getName();
	b.bookingId = c.getBooking().getId();
	b.calendarIds = c.getCalendarIds();
	b.cleaningCosts = Float.toString(c.getCleaningCosts());
	b.id = c.getId();
	b.black = c.isBlack();
	b.bookingId = c.getBooking().getId();
	if (c.getRoom() != null) {
	    b.room = c.getRoom().getName();
	}
	return b;
    }

}

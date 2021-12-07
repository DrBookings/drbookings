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

import com.github.drbookings.exception.NoSuchBookingException;
import com.github.drbookings.ser.CleaningBeanSer;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author Alexander Kerner
 * @date 2018-08-19
 *
 */
public class CleaningEntryFactory {

    private final CleaningFactory provider;
    private final RoomFactory roomFactory;
    private final Collection<? extends BookingBean> bookings;

    public CleaningEntryFactory(final Collection<? extends BookingBean> bookings, final CleaningFactory cleaningFactory,
	    final RoomFactory roomFactory) {

	provider = cleaningFactory;
	this.bookings = bookings;
	this.roomFactory = roomFactory;
    }

    public CleaningEntryFactory(final Collection<? extends BookingBean> bookings) {

	this(bookings, CleaningFactory.getInstance(), RoomFactory.getInstance());
    }

    public CleaningEntryFactory() {

	this(Collections.emptyList(), CleaningFactory.getInstance(), RoomFactory.getInstance());
    }

    public CleaningEntry createCleaning(final CleaningBeanSer c) {

	final CleaningEntry result = createCleaning(c.id, c.date, c.name, c.bookingId, c.room, c.cleaningCosts, c.black,
		c.calendarIds);
	return result;
    }

    public CleaningEntry createCleaning(final LocalDate date, final String name, final String roomName,
	    final float cleaningCosts, final boolean black) {

	return createCleaning(null, date, name, null, roomName, cleaningCosts, black, null);
    }

    public CleaningEntry createCleaning(final String id, final LocalDate date, final String name,
	    final String bookingId, final String roomName, final float cleaningCosts, final boolean black,
	    final List<String> calendarIds) {

	return createCleaning(id, date, name, bookingId, roomName, Float.toString(cleaningCosts), black, calendarIds);
    }

    public CleaningEntry createCleaning(final String id, final LocalDate date, final String name,
	    final String bookingId, final String roomName, final String cleaningCosts, final boolean black,
	    final List<String> calendarIds) {

	final CleaningEntry result = new CleaningEntry(id, date, findBookingById(bookingId),
		provider.getOrCreateElement(name), black, cleaningCosts);
	result.setRoom(roomFactory.getOrCreateElement(roomName));
	result.setCalendarIds(calendarIds);
	modifiers.forEach(m -> m.accept(result));
	// System.err.println(result);
	return result;
    }

    private final List<Consumer<CleaningEntry>> modifiers = new ArrayList<>();

    public void addModifier(final Consumer<CleaningEntry> e) {

	modifiers.add(e);
    }

    private BookingBean findBookingById(final String bookingId) {

	if (bookingId == null)
	    return null;
	final Optional<? extends BookingBean> o = bookings.stream().filter(e -> bookingId.equals(e.getId()))
		.findFirst();
	if (o.isPresent())
	    return o.get();
	throw new NoSuchBookingException("Not found for ID " + bookingId);
    }

    public List<CleaningEntry> build(final Collection<? extends CleaningBeanSer> elements) {

	return elements.stream().map(e -> createCleaning(e)).collect(Collectors.toList());
    }
}

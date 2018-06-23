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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.data.BookingOrigin;

public class BookingsByOrigin<T extends IBooking> {

    public static Predicate<IBooking> BOOKING_FILTER = b -> "booking".equalsIgnoreCase(b.getBookingOrigin().getName());

    public static Predicate<IBooking> AIRBNB_FILTER = b -> "airbnb".equalsIgnoreCase(b.getBookingOrigin().getName());

    public static Predicate<IBooking> OTHER_FILTER = BOOKING_FILTER.negate().and(AIRBNB_FILTER.negate());

    private final Collection<T> bookingEntries;

    public BookingsByOrigin(final Collection<? extends T> bookingEntries) {
	this.bookingEntries = new ArrayList<>(bookingEntries);

    }

    @Override
    public boolean equals(final Object o) {
	return bookingEntries.equals(o);
    }

    public Collection<T> getAirbnbBookings() {
	return bookingEntries.stream().filter(AIRBNB_FILTER).collect(Collectors.toList());
    }

    public Collection<T> getAllBookings() {
	return getAllBookings(false);
    }

    public Collection<T> getAllBookings(final boolean cheat) {
	if (cheat) {
	    return bookingEntries.stream().filter(b -> !StringUtils.isBlank(b.getBookingOrigin().getName()))
		    .collect(Collectors.toList());
	}
	return bookingEntries;
    }

    public Collection<T> getBookingBookings() {
	return bookingEntries.stream().filter(BOOKING_FILTER).collect(Collectors.toList());
    }

    public Collection<T> getByOriginName(final String name) {
	return bookingEntries.stream().filter(b -> name.equalsIgnoreCase(b.getBookingOrigin().getName()))
		.collect(Collectors.toList());
    }

    public Collection<T> getByOrigin(final BookingOrigin origin) {
	return bookingEntries.stream().filter(b -> origin.equals(b.getBookingOrigin())).collect(Collectors.toList());
    }

    public Map<BookingOrigin, Collection<T>> getMap() {
	final Map<BookingOrigin, Collection<T>> result = new LinkedHashMap<>();
	for (final T be : bookingEntries) {
	    final Collection<T> value = result.getOrDefault(be.getBookingOrigin(), new ArrayList<>());
	    value.add(be);
	}
	return result;
    }

    public Collection<T> getOtherBookings() {
	return bookingEntries.stream().filter(OTHER_FILTER).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
	return bookingEntries.hashCode();
    }

    public boolean isEmpty() {
	return bookingEntries.isEmpty();
    }

}

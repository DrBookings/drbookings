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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.model.BookingEntry;
import com.google.common.collect.Range;

public class Bookings {

    private static final Logger logger = LoggerFactory.getLogger(Bookings.class);

    public static long countNights(final Collection<? extends BookingBean> bookings) {
	return bookings.stream().filter(b -> !b.isSplitBooking()).mapToLong(b -> b.getNumberOfNights()).sum();
    }

    public static double getServiceFeePercentAmount(final BookingBean booking) {
	return (booking.getGrossEarnings() - booking.getCleaningFees()) * booking.getServiceFeesPercent() / 100.0;
    }

    public static List<BookingEntry> toEntries(final BookingBean... bookings) {
	return toEntries(Arrays.asList(bookings));
    }

    public static List<BookingEntry> toEntries(final Collection<? extends BookingBean> bookings) {
	final List<BookingEntry> result = new ArrayList<>();
	for (final BookingBean b : bookings) {
	    for (final LocalDate d : new DateRange(b.getCheckIn(), b.getCheckOut())) {
		result.add(new BookingEntry(d, b));
	    }
	}
	return result;
    }

    public static Range<LocalDate> getDateRange(final Collection<BookingBean> bookings) {
	if (bookings == null || bookings.isEmpty()) {
	    throw new IllegalArgumentException();
	}
	final LocalDate firstCheckIn = bookings.stream().map(BookingBean::getCheckIn).min((d1, d2) -> d1.compareTo(d2))
		.get();
	final LocalDate lastCheckOut = bookings.stream().map(BookingBean::getCheckOut).max((d1, d2) -> d1.compareTo(d2))
		.get();

	return Range.closed(firstCheckIn, lastCheckOut);
    }

    // public static long countCleanings(final Collection<? extends BookingBean>
    // bookings) {
    // return bookings.stream().filter(b -> b.getCleaning() != null).count();
    // }
    //
    // public static double getCleaningFees(final Collection<BookingBean> bookings)
    // {
    // return bookings.stream().filter(b -> b.getCleaning() != null).mapToDouble(b
    // -> b.getCleaningFees()).sum();
    // }
    //
    // public static double getCleaningCosts(final Collection<BookingBean> bookings)
    // {
    // if (logger.isDebugEnabled()) {
    // logger.debug("Cleaning costs for\n" + bookings.stream().map(i ->
    // i.toString())
    // .collect(Collectors.joining("\n")));
    // }
    // return bookings.stream().filter(b -> b.getCleaning() != null).map(b ->
    // b.getCleaning()).mapToDouble(c -> c.getCleaningCosts()).sum();
    // }
}

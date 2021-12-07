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

import com.github.drbookings.*;
import com.github.drbookings.model.BookingEntryToBooking;
import com.github.drbookings.ui.provider.OccupancyRateProvider;
import com.github.drbookings.ui.selection.DateBeanSelectionManager;
import com.google.common.collect.Range;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.OptionalDouble;

public class StatusLabelStringFactory {

    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,###,###,##0");

    private static final BookingEntryToBooking bookingEntryToBooking = new BookingEntryToBooking();

    private static String buildAirbnbStringNightCount(final BookingsByOrigin<BookingEntry> bookings,
	    final boolean complete) {
	return buildStringNightCount("Airbnb", bookings.getAirbnbBookings(), complete);
    }

    private static String buildBookingStringNightCount(final BookingsByOrigin<BookingEntry> bookings,
	    final boolean complete) {
	return buildStringNightCount("BookingBean", bookings.getBookingBookings(), complete);
    }

    private static String buildOtherStringNightCount(final BookingsByOrigin<BookingEntry> bookings,
	    final boolean complete) {
	return buildStringNightCount("Other", bookings.getOtherBookings(), complete);
    }

    private static String buildStringEarnings(final Collection<? extends BookingEntry> bookings,
	    final boolean completePayment, final boolean netEarnings) {
	return DECIMAL_FORMAT.format(getEarningsSum(bookings, netEarnings, completePayment));
    }

    private static String buildStringNightCount(final String prefix, final Collection<? extends BookingEntry> bookings,
	    final boolean complete) {
	if (complete) {
	    return prefix + ":" + bookingEntryToBooking.apply(bookings).stream().count();
	}
	return prefix + ":" + bookings.stream().filter(new NightCountFilter()).count();
    }

    private static Collection<? extends EarningsProvider> getEarningsProvider(
	    final Collection<? extends BookingEntry> bookings, final boolean completePayment) {
	Collection<? extends EarningsProvider> result;
	if (completePayment) {
	    result = bookingEntryToBooking.apply(bookings);
	} else {
	    result = bookings;
	}
	return result;
    }

    private static double getEarningsSum(final Collection<? extends BookingEntry> bookings, final boolean netEarnings,
	    final boolean completePayment) {

	return getEarningsProvider(bookings, completePayment).stream().mapToDouble(b -> b.getEarnings(netEarnings))
		.sum();
    }

    private final BookingsByOrigin<BookingEntry> bookings;

    public StatusLabelStringFactory(final BookingsByOrigin<BookingEntry> bookings) {
	this.bookings = bookings;
    }

    public String build() {
	final boolean completePayment = SettingsManager.getInstance().isCompletePayment();
	final boolean netEarnings = SettingsManager.getInstance().isShowNetEarnings();
	return build(completePayment, netEarnings);

    }

    private String build(final boolean completePayment, final boolean netEarnings) {
	final StringBuilder sb = new StringBuilder();
	// sb.append(BookingEntries.getMinDate(bookings.getAllBookings()).get());
	// sb.append(" ▶ ");
	// sb.append(BookingEntries.getMaxDate(bookings.getAllBookings()).get());
	// sb.append("\t");
	final Range<LocalDate> selectedRange = DateBeanSelectionManager.getInstance().getSelectedDateRange();
	if (selectedRange == null) {
	    return sb.toString();
	}
	sb.append("#unique nights: ");
	sb.append(LocalDates.getNumberOfNights(selectedRange.lowerEndpoint(), selectedRange.upperEndpoint()));
	sb.append("\tEarnings:");
	sb.append(DECIMAL_FORMAT.format(bookings.getAllBookings(false).stream().filter(b -> !b.isCheckOut())
		.mapToDouble(b -> b.getEarnings(netEarnings)).sum()));
	sb.append("\tAv.Earnings/Night/Room:");
	final OptionalDouble av = bookings.getAllBookings(false).stream().filter(b -> !b.isCheckOut())
		.mapToDouble(b -> b.getEarnings(netEarnings)).average();
	if (av.isPresent()) {
	    sb.append(DECIMAL_FORMAT.format(av.getAsDouble()));
	} else {
	    sb.append(DECIMAL_FORMAT.format(0));
	}
	sb.append("\tOccupancyRate:");
	sb.append(StatusLabelStringFactory.DECIMAL_FORMAT.format(new OccupancyRateProvider().getOccupancyRate() * 100));
	// sb.append("\tMinPriceAtRate:");
	// sb.append(StatusLabelStringFactory.DECIMAL_FORMAT.format(new
	// MinimumPriceProvider().getMinimumPrice()));
	return sb.toString();
    }

}

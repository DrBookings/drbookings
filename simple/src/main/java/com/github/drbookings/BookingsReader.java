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

import com.github.drbookings.ser.DataStoreCoreSer;

import java.util.List;
import java.util.stream.Collectors;

public class BookingsReader extends AbstractReader {

    public BookingsByOrigin<BookingBean> read(final DataStoreCoreSer dataSer) {
	final BookingBeanFactory f = new BookingBeanFactory();
	final List<BookingBean> bookings2 = dataSer.getBookingsSer().stream()
		.filter(e -> (getStartDate() == null) || e.checkOutDate.isAfter(getStartDate().minusDays(1)))
		.filter(e -> (getEndDate() == null) || e.checkOutDate.isBefore(getEndDate().plusDays(1)))
		.map(e -> f.createBooking(e)).collect(Collectors.toList());
	return new BookingsByOrigin<>(bookings2);

    }

}

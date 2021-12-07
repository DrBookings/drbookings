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

package com.github.drbookings.data.numbers.earnings;

import java.util.Collection;

import com.github.drbookings.IBooking;
import com.github.drbookings.data.numbers.AbstractFromBookingsProvider;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-21
 *
 */
@Deprecated
public class NetEarningsProvider extends AbstractFromBookingsProvider<IBooking> {

    public NetEarningsProvider(final Collection<? extends IBooking> bookings) {
	super(bookings);

    }

    @Override
    public Number call() throws Exception {
	return bookings.stream().mapToDouble(b -> b.getNetEarnings()).sum();
    }

}

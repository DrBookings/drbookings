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

package com.github.drbookings.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.drbookings.model.data.BookingBean;

public class BookingEntryToBooking implements Function<Collection<? extends BookingEntry>, Set<BookingBean>> {

    public Set<BookingBean> apply(final BookingEntry t) {
	return apply(Arrays.asList(t));
    }

    @Override
    public Set<BookingBean> apply(final Collection<? extends BookingEntry> t) {
	return t.stream().map(b -> b.getElement()).collect(Collectors.toSet());
    }

}

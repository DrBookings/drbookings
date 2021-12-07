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

package com.github.drbookings.ui.controller;

import com.github.drbookings.BookingEntry;
import com.github.drbookings.BookingEntryBin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public abstract class BookingEntryBins<T> {

    protected Collection<? extends BookingEntry> elements = new ArrayList<>();

    public BookingEntryBins(final Collection<? extends BookingEntry> elements) {
	this.elements = Objects.requireNonNull(elements);
    }

    public abstract Collection<BookingEntryBin<T>> getBins();
}

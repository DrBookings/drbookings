/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.ui.BookingEntry;

import java.util.Collection;
import java.util.Objects;

public class BookingEntryBin<T> {

    private final T label;

    private final Collection<? extends BookingEntry> entries;

    public BookingEntryBin(T label, Collection<? extends BookingEntry> entries) {
        this.label = Objects.requireNonNull(label);
        this.entries = Objects.requireNonNull(entries);
    }

    public Collection<? extends BookingEntry> getEntries() {
        return entries;
    }

    public T getLabel() {
        return label;
    }
}

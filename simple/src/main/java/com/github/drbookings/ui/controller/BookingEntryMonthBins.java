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

package com.github.drbookings.ui.controller;

import com.github.drbookings.BookingEntryBin;
import com.github.drbookings.ui.BookingEntry;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;

public class BookingEntryMonthBins extends BookingEntryBins<YearMonth> {

    public BookingEntryMonthBins(Collection<? extends BookingEntry> elements) {
        super(elements);
    }

    @Override
    public Collection<BookingEntryBin<YearMonth>> getBins() {
        Multimap<YearMonth, BookingEntry> result = ArrayListMultimap.create();
        elements.forEach(e -> result.put(YearMonth.from(e.getDate()), e));
        Collection<BookingEntryBin<YearMonth>> result2 = new ArrayList<>();
        result.asMap().forEach((k, v) -> result2.add(new BookingEntryBin<>(k, v)));
        return result2;
    }
}

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.drbookings.BookingEntry;
import com.github.drbookings.BookingOrigin;
import com.github.drbookings.SettingsManager;

public class NightlyRateView {

    final Map<BookingOrigin, Map<LocalDate, Collection<Number>>> data = new LinkedHashMap<>();
    private double binSize = 1;

    private BinType binType;

    public NightlyRateView() {

    }

    public NightlyRateView(final Collection<? extends BookingEntry> bookingEntries) {
	addAll(bookingEntries);
    }

    public NightlyRateView add(final BookingEntry b) {
	Map<LocalDate, Collection<Number>> innerMap = data.get(b.getBookingOrigin());
	if (innerMap == null) {
	    innerMap = new LinkedHashMap<>();
	    data.put(b.getBookingOrigin(), innerMap);
	}
	Collection<Number> values = innerMap.get(b.getDate());
	if (values == null) {
	    values = new ArrayList<>();
	}
	values.add(b.getEarnings(SettingsManager.getInstance().isShowNetEarnings()));
	innerMap.put(b.getDate(), values);
	return this;
    }

    public NightlyRateView addAll(final Collection<? extends BookingEntry> bookingEntries) {
	bookingEntries.forEach(c -> add(c));
	return this;
    }

    public Map<BookingOrigin, Map<LocalDate, Collection<Number>>> getData() {
	return Collections.unmodifiableMap(data);
    }

    public void setBinSize(final double binSize) {
	this.binSize = binSize;
    }

    public void setBinType(final BinType selectedItem) {
	binType = binType;
    }

    @Override
    public String toString() {
	return "NightlyRateView{" + "data=" + data + '}';
    }
}

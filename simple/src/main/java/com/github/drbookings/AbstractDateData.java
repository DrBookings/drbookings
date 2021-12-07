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

import org.apache.commons.collections4.keyvalue.MultiKey;
import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.collections4.map.MultiKeyMap;

import java.time.LocalDate;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

public class AbstractDateData<T extends RoomDateProvider> implements BaseDateData<T> {

    /**
     * (Room name, Date) -> Value
     */
    private final MultiKeyMap<Object, T> entries;

    public AbstractDateData() {
	entries = MultiKeyMap.multiKeyMap(new LinkedMap<>());
    }

    @Override
    public void add(final T e) {
	if (e.getRoom() == null) {
	    throw new NullPointerException("No room available");
	}
	entries.put(getMultiKey(e.getRoom().getName(), e.getDate()), e);

    }

    @Override
    public void addAll(final Collection<? extends T> elements) {
	for (final T ce : elements) {
	    add(ce);
	}
    }

    @Override
    public SortedSet<LocalDate> allDates() {
	final TreeSet<LocalDate> result = new TreeSet<>();
	entries.forEach((k, v) -> result.add((LocalDate) k.getKey(1)));
	return result;
    }

    @Override
    public T get(final LocalDate date, final String roomName) {
	return entries.get(getMultiKey(roomName, date));
    }

    public MultiKeyMap<Object, T> getEntries() {
	// TODO: make protected
	return entries;
    }

    public MultiKey<Object> getMultiKey(final String roomName, final LocalDate date) {
	// TODO: make protected
	return new MultiKey<>(roomName, date);
    }

    @Override
    public int size() {
	return entries.size();
    }

    @Override
    public Collection<T> getAll() {
	return entries.values();
    }

}

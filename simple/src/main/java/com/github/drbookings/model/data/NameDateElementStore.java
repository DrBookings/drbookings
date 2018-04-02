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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class NameDateElementStore<T> {

    /**
     * Name -> Date -> Entry
     */
    private final Map<String, Map<LocalDate, T>> entries;

    private final BiFunction<String, LocalDate, T> newElementSupplier;

    public NameDateElementStore(BiFunction<String, LocalDate, T> newElementSupplier) {
        entries = new LinkedHashMap<>();
        this.newElementSupplier = newElementSupplier;
    }

    public T add(String name, LocalDate date){
        Map<LocalDate, T> map = entries.computeIfAbsent(name, k -> new LinkedHashMap<>());
        T entry = map.computeIfAbsent(date, k -> newElementSupplier.apply(name, date));
        return entry;
    }


}

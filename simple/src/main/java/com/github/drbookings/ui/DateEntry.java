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

import java.time.LocalDate;
import java.util.Objects;

/**
 * A manifestation of some element of type {@code E} at a certain date.
 *
 * @param <E> the type of element
 * @see CleaningEntry
 * @see BookingEntry
 */
public class DateEntry<E> implements Comparable<DateEntry<E>> {

    private final LocalDate date;

    private final E e;

    public DateEntry(final LocalDate date, final E element) {
        super();
        this.date = date;
        this.e = element;
    }

    @Override
    public int compareTo(final DateEntry<E> o) {
        return getDate().compareTo(o.getDate());
    }


    public LocalDate getDate() {
        return date;
    }

    public E getElement() {
        return e;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DateEntry<?> dateEntry = (DateEntry<?>) o;
        return Objects.equals(getDate(), dateEntry.getDate()) &&
            Objects.equals(e, dateEntry.e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDate(), e);
    }

    @Override
    public String toString() {
        return e.toString();
    }

}

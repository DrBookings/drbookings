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

import com.github.drbookings.model.data.Room;

import java.time.LocalDate;
import java.util.Objects;

public class DateRoomEntry<E> extends DateEntry<E> {

    private final Room room;

    public DateRoomEntry(final LocalDate date, final Room room, final E element) {
	super(date, element);
	this.room = room;
    }

    public Room getRoom() {
	return room;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateRoomEntry)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final DateRoomEntry<?> that = (DateRoomEntry<?>) o;
        return Objects.equals(getRoom(), that.getRoom());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getRoom());
    }
}

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

import com.github.drbookings.model.RoomEntry;

import java.time.LocalDate;
import java.util.Objects;

public class DateRoomEntry<E> extends DateEntry<E> {

    private final Room room;

    public DateRoomEntry(final RoomEntry room, final E element) {
        super(room.getDate(), element);
        this.room = room.getElement();
    }

    public DateRoomEntry(LocalDate date, Room room, E element) {
        this(new RoomEntry(date, room), element);
    }

    public Room getRoom() {
        return room;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DateRoomEntry<?> that = (DateRoomEntry<?>) o;
        return Objects.equals(getRoom(), that.getRoom());
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), getRoom());
    }
}

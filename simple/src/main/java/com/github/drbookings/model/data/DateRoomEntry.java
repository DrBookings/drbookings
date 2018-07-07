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
import java.util.Objects;

import com.github.drbookings.model.RoomEntry;

public class DateRoomEntry<E> extends DateEntryImpl<E> {

    private final Room room;

    public DateRoomEntry(final LocalDate date, final Room room, final E element) {
	this(new RoomEntry(date, room), element);
    }

    public DateRoomEntry(final RoomEntry room, final E element) {
	super(room.getDate(), element);
	this.room = room.getElement();
    }

    @Override
    public boolean equals(final Object o) {
	if (this == o) {
	    return true;
	}
	if ((o == null) || (getClass() != o.getClass())) {
	    return false;
	}
	if (!super.equals(o)) {
	    return false;
	}
	final DateRoomEntry<?> that = (DateRoomEntry<?>) o;
	return Objects.equals(getRoom(), that.getRoom());
    }

    public Room getRoom() {
	return room;
    }

    @Override
    public int hashCode() {

	return Objects.hash(super.hashCode(), getRoom());
    }
}

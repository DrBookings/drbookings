package com.github.drbookings.ui;

import java.time.LocalDate;

import com.github.drbookings.model.data.Room;

public class DateRoomEntry<E> extends DateEntry<E> {

    private final Room room;

    public DateRoomEntry(final LocalDate date, final Room room, final E element) {
	super(date, element);
	this.room = room;
    }

    public Room getRoom() {
	return room;
    }

}

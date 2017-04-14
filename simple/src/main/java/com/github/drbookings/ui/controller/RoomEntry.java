package com.github.drbookings.ui.controller;

import java.time.LocalDate;

import com.github.drbookings.model.data.Room;

public class RoomEntry extends DateEntry<Room> {

    public RoomEntry(final LocalDate date, final Room element) {
	super(date, element);
    }

}

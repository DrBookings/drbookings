package com.github.drbookings.model.manager;

import com.github.drbookings.model.data.Room;

public class RoomManager extends NamedManager<Room> {

    @Override
    protected Room buildNewElement(final String name) {
	return new Room(name);
    }

}

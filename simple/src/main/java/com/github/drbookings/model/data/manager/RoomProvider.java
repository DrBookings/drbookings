package com.github.drbookings.model.data.manager;

import com.github.drbookings.model.data.Room;

public class RoomProvider extends NamedProvider<Room> {

    @Override
    protected Room buildNewElement(final String name) {
	return new Room(name);
    }

}

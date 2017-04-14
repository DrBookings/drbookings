package com.github.drbookings.model.data.manager;

import com.github.drbookings.model.data.Guest;

public class GuestProvider extends NamedProvider<Guest> {

    @Override
    protected Guest buildNewElement(final String name) {
	return new Guest(name);
    }

}

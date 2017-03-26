package com.github.drbookings.model.manager;

import com.github.drbookings.model.data.Guest;

public class GuestManager extends NamedManager<Guest> {

    @Override
    protected Guest buildNewElement(final String name) {
	return new Guest(name);
    }

}

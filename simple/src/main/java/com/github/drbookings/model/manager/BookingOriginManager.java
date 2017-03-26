package com.github.drbookings.model.manager;

import com.github.drbookings.model.data.BookingOrigin;

public class BookingOriginManager extends NamedManager<BookingOrigin> {

    @Override
    protected BookingOrigin buildNewElement(final String name) {
	return new BookingOrigin(name);
    }

}

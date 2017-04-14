package com.github.drbookings.model.data.manager;

import com.github.drbookings.model.data.BookingOrigin;

public class BookingOriginProvider extends NamedProvider<BookingOrigin> {

    @Override
    protected BookingOrigin buildNewElement(final String name) {
	return new BookingOrigin(name);
    }

}

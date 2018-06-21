package com.github.drbookings.data.numbers;

import java.util.Collection;
import java.util.concurrent.Callable;

import com.github.drbookings.model.IBooking;

public abstract class AbstractFromBookingsProvider implements Callable<Number> {

    protected final Collection<? extends IBooking> bookings;

    public AbstractFromBookingsProvider(final Collection<? extends IBooking> bookings) {
	this.bookings = bookings;
    }

}

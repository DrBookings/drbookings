package com.github.drbookings.data.numbers;

import java.util.Collection;
import java.util.concurrent.Callable;

import com.github.drbookings.model.IBooking;

public abstract class AbstractFromBookingsProvider<T extends IBooking> implements Callable<Number> {

    protected final Collection<? extends T> bookings;

    public AbstractFromBookingsProvider(final Collection<? extends T> bookings) {
	this.bookings = bookings;
    }

}

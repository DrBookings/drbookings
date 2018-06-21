package com.github.drbookings.data.numbers.earnings;

import java.util.Collection;

import com.github.drbookings.data.numbers.AbstractFromBookingsProvider;
import com.github.drbookings.model.IBooking;

public class GrossEarningsProvider extends AbstractFromBookingsProvider {

    public GrossEarningsProvider(final Collection<? extends IBooking> bookings) {
	super(bookings);
    }

    @Override
    public Number call() throws Exception {
	return bookings.stream().mapToDouble(b -> b.getGrossEarnings()).sum();
    }

}

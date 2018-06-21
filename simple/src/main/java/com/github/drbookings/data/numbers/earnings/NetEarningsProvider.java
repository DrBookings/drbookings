package com.github.drbookings.data.numbers.earnings;

import java.util.Collection;

import com.github.drbookings.data.numbers.AbstractFromBookingsProvider;
import com.github.drbookings.model.IBooking;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-21
 *
 */
public class NetEarningsProvider extends AbstractFromBookingsProvider<IBooking> {

    public NetEarningsProvider(final Collection<? extends IBooking> bookings) {
	super(bookings);

    }

    @Override
    public Number call() throws Exception {
	return bookings.stream().mapToDouble(b -> b.getNetEarnings()).sum();
    }

}

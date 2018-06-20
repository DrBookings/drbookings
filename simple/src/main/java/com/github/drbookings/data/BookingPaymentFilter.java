package com.github.drbookings.data;

import java.time.YearMonth;

import com.github.drbookings.model.BookingEntry;

/**
 * Booking.com pays bookings once per month by check-out date.
 *
 * @author Alexander Kerner
 *
 */
public class BookingPaymentFilter extends PaymentFilter {

    public BookingPaymentFilter() {
	super();

    }

    public BookingPaymentFilter(final YearMonth month) {
	super(month);

    }

    @Override
    public boolean test(final BookingEntry booking) {
	if (getMonth() == null) {
	    return new SimplePaymentFilter().test(booking);
	}
	return YearMonth.from(booking.getElement().getCheckOut()).equals(getMonth());
    }

}

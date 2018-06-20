package com.github.drbookings.data;

import java.time.YearMonth;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.drbookings.model.BookingEntry;

/**
 *
 * @author Alexander Kerner
 *
 */
public class SimplePaymentFilter extends PaymentFilter {

    /**
     * Creates a new filter that filters by given payment month. If {@code null},
     * entries will be filtered by {@link BookingEntry#isPaymentDone()}.
     *
     * @param month
     *            The payment month
     */
    public SimplePaymentFilter(final YearMonth month) {
	super(month);
    }

    /**
     * Creates a new filter that filters by {@link BookingEntry#isPaymentDone()}.
     */
    public SimplePaymentFilter() {
	super();
    }

    @Override
    public boolean test(final BookingEntry booking) {
	if (getMonth() == null) {
	    return booking.getElement().isPaymentDone();
	}
	// get all payment months
	final Set<YearMonth> paymentDates = booking.getElement().getPayments().stream()
		.map(p -> YearMonth.from(p.getDate())).collect(Collectors.toSet());

	// check if one payment month equals given month
	if (paymentDates.contains(getMonth())) {
	    // check if given booking was payed in given month
	    return getMonth().equals(YearMonth.from(booking.getDate()));
	}
	return false;
    }
}

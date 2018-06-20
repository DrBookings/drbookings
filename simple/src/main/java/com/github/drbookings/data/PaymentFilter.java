package com.github.drbookings.data;

import java.time.YearMonth;
import java.util.function.Predicate;

import com.github.drbookings.model.BookingEntry;

/**
 * A {@link Predicate} to filter for bookings that should be payed.
 *
 * @author Alexander Kerner
 *
 */
public abstract class PaymentFilter implements Predicate<BookingEntry> {

    private final YearMonth month;

    /**
     * Creates a new filter that filters by given payment month. If {@code null},
     * entries will be filtered by {@link BookingEntry#isPaymentDone()}.
     *
     * @param month
     *            The payment month
     */
    public PaymentFilter(final YearMonth month) {

	this.month = month;
    }

    /**
     * Creates a new filter that filters by {@link BookingEntry#isPaymentDone()}.
     */
    public PaymentFilter() {

	this(null);
    }

    protected YearMonth getMonth() {
	return month;
    }

}

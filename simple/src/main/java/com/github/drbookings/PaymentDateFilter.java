package com.github.drbookings;

import java.time.LocalDate;
import java.util.function.Predicate;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.ui.BookingEntry;
import com.google.common.collect.Range;

public class PaymentDateFilter implements Predicate<BookingEntry> {

	private final Range<LocalDate> dates;

	public PaymentDateFilter(final Range<LocalDate> dates) {
		this.dates = dates;
		System.err.println("Date range " + dates);
	}

	@Override
	public boolean test(final BookingEntry be) {
		final Booking t = be.getElement();
		return t.getDateOfPayment() != null && t.getDateOfPayment().isAfter(dates.lowerEndpoint().minusDays(1))
				&& t.getDateOfPayment().isBefore(dates.upperEndpoint().plusDays(1));
	}

}

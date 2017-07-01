package com.github.drbookings.model;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.drbookings.ui.BookingEntry;

public class PayoutCalculator implements Function<Collection<? extends BookingEntry>, Payout> {

	public PayoutCalculator() {

	}

	@Override
	public Payout apply(final Collection<? extends BookingEntry> bookings) {
		final List<Payout> result = bookings.stream().map(b -> Payout.build(b)).collect(Collectors.toList());
		return collapse(result);
	}

	private Payout collapse(final List<Payout> payouts) {
		if (payouts == null) {
			throw new NullPointerException();
		}
		if (payouts.isEmpty()) {
			throw new IllegalArgumentException();
		}
		Payout result = payouts.get(0);
		final Iterator<Payout> it = payouts.subList(1, payouts.size()).iterator();
		while (it.hasNext()) {
			result = result.merge(it.next());
		}
		return result;
	}

}

package com.github.drbookings.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.ui.BookingEntry;

public class BookingEntryToBooking implements Function<Collection<? extends BookingEntry>, Set<Booking>> {

    public Set<Booking> apply(final BookingEntry t) {
	return apply(Arrays.asList(t));
    }

    @Override
    public Set<Booking> apply(final Collection<? extends BookingEntry> t) {
	return t.stream().map(b -> b.getElement()).collect(Collectors.toSet());
    }

}

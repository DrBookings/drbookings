package com.github.drbookings.model.data;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.drbookings.ui.BookingEntry;

public class BookingEntries {

	public static Predicate<BookingEntry> HAS_CLEANING = b -> b.getElement().getCleaning() != null;

	public static long countNights(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(b -> !b.isCheckOut()).count();
	}

	public static long countCleanings(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(HAS_CLEANING).map(b -> b.getElement().getCleaning()).collect(Collectors.toSet())
				.size();
	}

	public static double getCleaningCosts(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(HAS_CLEANING).map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.mapToDouble(b -> b.getCleaning().getCleaningCosts()).sum();
	}

	public static double getCleaningFees(final Collection<? extends BookingEntry> bookings) {
		return bookings.stream().filter(HAS_CLEANING).map(b -> b.getElement()).collect(Collectors.toSet()).stream()
				.mapToDouble(b -> b.getCleaningFees()).sum();
	}
}

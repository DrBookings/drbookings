package com.github.drbookings.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.data.BookingOrigin;

public class BookingsByOrigin<T extends IBooking> {

	public static Predicate<IBooking> BOOKING_FILTER = b -> "booking"
			.equalsIgnoreCase(b.getElement().getBookingOrigin().getName());

	public static Predicate<IBooking> AIRBNB_FILTER = b -> "airbnb"
			.equalsIgnoreCase(b.getElement().getBookingOrigin().getName());

	public static Predicate<IBooking> OTHER_FILTER = BOOKING_FILTER.negate().and(AIRBNB_FILTER.negate());

	private final Collection<T> bookingEntries;

	public BookingsByOrigin(final Collection<? extends T> bookingEntries) {
		this.bookingEntries = new ArrayList<>(bookingEntries);
	}

	public Collection<T> getAirbnbBookings() {
		return bookingEntries.stream().filter(AIRBNB_FILTER).collect(Collectors.toList());
	}

	public Collection<T> getAllBookings() {
		return bookingEntries;
	}

	public Collection<T> getBookingBookings() {
		return bookingEntries.stream().filter(BOOKING_FILTER).collect(Collectors.toList());
	}

	public Collection<T> getByOriginName(final String name) {
		return bookingEntries.stream().filter(b -> name.equalsIgnoreCase(b.getElement().getBookingOrigin().getName()))
				.collect(Collectors.toList());
	}

	public Map<BookingOrigin, Collection<T>> getMap() {
		final Map<BookingOrigin, Collection<T>> result = new LinkedHashMap<>();
		for (final T be : bookingEntries) {
			final Collection<T> value = result.getOrDefault(be.getElement().getBookingOrigin(), new ArrayList<>());
			value.add(be);
			result.put(be.getElement().getBookingOrigin(), value);
		}
		return result;
	}

	public Collection<T> getOtherBookings() {
		return bookingEntries.stream().filter(OTHER_FILTER).collect(Collectors.toList());
	}

}

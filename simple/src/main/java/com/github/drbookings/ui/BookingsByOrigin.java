package com.github.drbookings.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.data.BookingOrigin;

public class BookingsByOrigin<T extends IBooking> implements Collection<T> {

	public static Predicate<IBooking> BOOKING_FILTER = b -> "booking"
			.equalsIgnoreCase(b.getElement().getBookingOrigin().getName());

	public static Predicate<IBooking> AIRBNB_FILTER = b -> "airbnb"
			.equalsIgnoreCase(b.getElement().getBookingOrigin().getName());

	public static Predicate<IBooking> OTHER_FILTER = BOOKING_FILTER.negate().and(AIRBNB_FILTER.negate());

	private final Collection<T> bookingEntries;

	public BookingsByOrigin(final Collection<? extends T> bookingEntries) {
		this.bookingEntries = new ArrayList<>(bookingEntries);
	}

	public BookingsByOrigin(final Collection<? extends T> bookingEntries, final boolean cheat) {
		if (cheat) {
			this.bookingEntries = bookingEntries.stream()
					.filter(b -> !StringUtils.isBlank(b.getElement().getBookingOrigin().getName()))
					.collect(Collectors.toList());
		} else {
			this.bookingEntries = new ArrayList<>(bookingEntries);
		}
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

	@Override
	public int size() {
		return bookingEntries.size();
	}

	@Override
	public boolean isEmpty() {
		return bookingEntries.isEmpty();
	}

	@Override
	public boolean contains(final Object o) {
		return bookingEntries.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return bookingEntries.iterator();
	}

	@Override
	public Object[] toArray() {
		return bookingEntries.toArray();
	}

	@Override
	public <T> T[] toArray(final T[] a) {
		return bookingEntries.toArray(a);
	}

	@Override
	public boolean add(final T e) {
		return bookingEntries.add(e);
	}

	@Override
	public boolean remove(final Object o) {
		return bookingEntries.remove(o);
	}

	@Override
	public boolean containsAll(final Collection<?> c) {
		return bookingEntries.containsAll(c);
	}

	@Override
	public boolean addAll(final Collection<? extends T> c) {
		return bookingEntries.addAll(c);
	}

	@Override
	public boolean removeAll(final Collection<?> c) {
		return bookingEntries.removeAll(c);
	}

	@Override
	public boolean retainAll(final Collection<?> c) {
		return bookingEntries.retainAll(c);
	}

	@Override
	public void clear() {
		bookingEntries.clear();
	}

	@Override
	public boolean equals(final Object o) {
		return bookingEntries.equals(o);
	}

	@Override
	public int hashCode() {
		return bookingEntries.hashCode();
	}

}

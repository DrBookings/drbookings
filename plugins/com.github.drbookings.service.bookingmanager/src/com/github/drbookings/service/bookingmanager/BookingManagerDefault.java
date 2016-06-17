package com.github.drbookings.service.bookingmanager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.core.api.BookingManager;
import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.api.BookingDay;
import com.github.drbookings.core.datamodel.impl.Bookings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookingManagerDefault implements BookingManager {

	private final static Logger log = LoggerFactory.getLogger(BookingManagerDefault.class);

	private final ObservableList<BookingDay> bookingDays = FXCollections.observableArrayList();

	@Override
	public BookingManagerDefault addAllBookings(final Collection<? extends Booking> bookings) {
		for (final Booking b : bookings) {
			addBooking(b);
		}
		return this;

	}

	@Override
	public BookingManagerDefault addBooking(final Booking booking) {
		final Map<LocalDate, BookingDay> map = buildMap();
		final ObservableList<BookingDay> bookingDays = new Bookings(Arrays.asList(booking)).toBookingDays();
		for (final BookingDay bd : bookingDays) {
			final BookingDay bookingDay = map.get(bd.getDate().getValue());
			if (bookingDay == null) {
				this.bookingDays.add(bd);
			} else {
				bookingDay.getBookings().addAll(bd.getBookings());
			}
		}
		return this;
	}

	private Map<LocalDate, BookingDay> buildMap() {
		final Map<LocalDate, BookingDay> result = new HashMap<>();

		for (final BookingDay bd : bookingDays) {
			result.put(bd.getDate().getValue(), bd);
		}

		return result;
	}

	@Override
	public ObservableList<BookingDay> getBookings() {
		return bookingDays;
	}

}

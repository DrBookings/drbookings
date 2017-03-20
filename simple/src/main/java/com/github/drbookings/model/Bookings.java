package com.github.drbookings.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.RoomBean;

public class Bookings {

    public static List<BookingBean> getAll(final Collection<? extends RoomBean> rooms) {
	final List<BookingBean> result = new ArrayList<>();
	for (final RoomBean rb : rooms) {
	    result.addAll(rb.getBookings());
	}
	return result;
    }

    public static String getRegexAllElse() {
	return "(?!airbnb|booking)";
    }

    public static Set<String> guestNameView(final Collection<? extends BookingBean> bookings) {
	return bookings.stream().map(g -> g.getGuestName()).collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static boolean hasCheckIn(final Collection<? extends BookingBean> bookings) {
	for (final BookingBean bb : bookings) {
	    if (bb.isCheckIn()) {
		return true;
	    }
	}
	return false;
    }

    public static boolean hasCheckOut(final Collection<? extends BookingBean> bookings) {
	for (final BookingBean bb : bookings) {
	    if (bb.isCheckOut()) {
		return true;
	    }
	}
	return false;
    }

    public static Set<RoomBean> roomsView(final Collection<? extends BookingBean> bookings) {
	final Set<RoomBean> rooms = new LinkedHashSet<>();
	for (final BookingBean bb : bookings) {
	    rooms.add(bb.getRoom());
	}
	return rooms;
    }

    public static List<BookingBean> viewCheckIn(final List<BookingBean> bookings) {
	return bookings.stream().filter(b -> b.isCheckIn()).collect(Collectors.toList());
    }

    public static List<BookingBean> viewCheckOut(final List<BookingBean> bookings) {
	return bookings.stream().filter(b -> b.isCheckOut()).collect(Collectors.toList());
    }

}

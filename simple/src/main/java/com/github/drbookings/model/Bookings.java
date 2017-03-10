package com.github.drbookings.model;

import java.util.List;
import java.util.stream.Collectors;

import com.github.drbookings.model.bean.BookingBean;

public class Bookings {

    public static List<String> guestNameView(final List<BookingBean> bookings) {
	return bookings.stream().map(g -> g.getGuestName()).collect(Collectors.toList());
    }

}

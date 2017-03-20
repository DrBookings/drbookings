package com.github.drbookings.model.bean;

import java.util.function.Predicate;

public class ConnectedBookingFilter implements Predicate<BookingBean> {

    private final BookingBean bookingBean;

    public ConnectedBookingFilter(final BookingBean bookingBean) {
	this.bookingBean = bookingBean;
    }

    @Override
    public boolean test(final BookingBean t) {

	return t.getGuestName().equals(bookingBean.getGuestName())
		&& t.getBruttoEarnings() == bookingBean.getBruttoEarnings();
    }

}

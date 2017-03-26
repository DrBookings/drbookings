package com.github.drbookings.model;

import java.util.function.Predicate;

import com.github.drbookings.model.bean.BookingBean;

public class BookingSourceFilter implements Predicate<BookingBean> {

    private final String bookingSourceRegex;

    public BookingSourceFilter(final String bookingSourceRegex) {
	super();
	this.bookingSourceRegex = bookingSourceRegex;
    }

    @Override
    public boolean test(final BookingBean t) {
	return t.getSource().matches(bookingSourceRegex);
    }

}

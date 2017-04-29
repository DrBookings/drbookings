package com.github.drbookings.ui;

import java.util.function.Predicate;

public class BookingFilter implements Predicate<BookingEntry> {

    private String filterString;

    public BookingFilter(final String filterString) {
	this.filterString = filterString;
    }

    public String getFilterString() {
	return filterString;
    }

    public void setFilterString(final String filterString) {
	this.filterString = filterString;
    }

    @Override
    public boolean test(final BookingEntry t) {
	if (filterString == null || filterString.length() < 1) {
	    return true;
	}
	boolean result = t.getElement().getGuest().getName().toLowerCase().contains(filterString.toLowerCase());
	if (!result) {
	    result = t.getDate() != null
		    && t.getDate().getMonth().toString().toLowerCase().contains(filterString.toLowerCase());

	}
	if (!result) {
	    result = t.getElement().getBookingOrigin().getName().toLowerCase().contains(filterString.toLowerCase());
	}
	return result;
    }

}

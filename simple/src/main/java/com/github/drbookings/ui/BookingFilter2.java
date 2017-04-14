package com.github.drbookings.ui;

import java.util.function.Predicate;

import com.github.drbookings.model.data.Booking;

public class BookingFilter2 implements Predicate<Booking> {

    private String filterString;

    public BookingFilter2(final String filterString) {
	this.filterString = filterString;
    }

    public String getFilterString() {
	return filterString;
    }

    public void setFilterString(final String filterString) {
	this.filterString = filterString;
    }

    @Override
    public boolean test(final Booking t) {
	if (filterString == null || filterString.length() < 1) {
	    return true;
	}
	boolean result = t.getGuest().getName().toLowerCase().contains(filterString.toLowerCase());
	if (!result) {
	    result = t.getCheckIn().getMonth().toString().toLowerCase().contains(filterString.toLowerCase());
	}
	if (!result) {
	    result = t.getBookingOrigin().getName().toLowerCase().contains(filterString.toLowerCase());
	}
	return result;
    }

}

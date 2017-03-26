package com.github.drbookings.model.ui;

import java.util.function.Predicate;

import com.github.drbookings.model.bean.BookingBean;

public class BookingFilter implements Predicate<BookingBean> {

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
    public boolean test(final BookingBean t) {
	if (filterString == null || filterString.length() < 1) {
	    return true;
	}
	boolean result = t.getGuestName().toLowerCase().contains(filterString.toLowerCase());
	if (!result) {
	    // final List<BookingBean> allSame =
	    // t.getRoom().getDateBean().getDataModel().getAllSame(t);
	    // final List<LocalDate> datesView = Bookings.datesView(allSame);
	    // for (final LocalDate d : datesView) {
	    // if
	    // (d.getMonth().toString().toLowerCase().contains(filterString.toLowerCase()))
	    // {
	    // return true;
	    // }
	    // }

	    result = t.getDate() != null
		    && t.getDate().getMonth().toString().toLowerCase().contains(filterString.toLowerCase());

	}
	if (!result) {
	    result = t.getSource().toLowerCase().contains(filterString.toLowerCase());
	}
	return result;
    }

}

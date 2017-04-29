package com.github.drbookings.ui;

import java.util.Collection;

public class BookingEntryView {

    protected final Collection<BookingEntry> bookingEntries;

    public BookingEntryView(final Collection<BookingEntry> bookingEntries) {
	super();
	this.bookingEntries = bookingEntries;
    }

    @Override
    public String toString() {
	return bookingEntries.toString();
    }

}

package com.github.drbookings.ui;

import java.util.Collection;
import java.util.Iterator;

public class GuestNameAndBookingOriginView extends BookingEntryView {

    public GuestNameAndBookingOriginView(final Collection<BookingEntry> bookingEntries) {
	super(bookingEntries);
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder();
	final Iterator<BookingEntry> it = bookingEntries.iterator();
	while (it.hasNext()) {
	    final BookingEntry b = it.next();
	    sb.append(b.getElement().getGuest().getName());
	    sb.append(" (");
	    sb.append(b.getElement().getBookingOrigin().getName());
	    sb.append(")");

	    if (it.hasNext()) {
		sb.append("\n");
	    }
	}

	return sb.toString();
    }

    public boolean isEmpty() {
	return bookingEntries.isEmpty();
    }

}

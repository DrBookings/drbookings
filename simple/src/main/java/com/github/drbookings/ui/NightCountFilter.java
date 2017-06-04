package com.github.drbookings.ui;

import java.util.function.Predicate;

public class NightCountFilter implements Predicate<BookingEntry> {

    @Override
    public boolean test(final BookingEntry t) {

	// boolean result = !t.isCheckOut() && !(t.isCheckIn() &&
	// LocalDates.isLastDayOfMonth(t.getDate()));

	// final boolean result = !t.isCheckOut()
	// ||
	// !(t.getElement().getCheckIn().getMonth().equals(t.getElement().getCheckOut().getMonth()));

	final boolean result = !t.isCheckOut();

	return result;

	// check-out plus check-in another month than check-out
	// return !(t.isCheckOut()
	// &&
	// !t.getElement().getCheckIn().getMonth().equals(t.getElement().getCheckOut().getMonth()));
    }

}

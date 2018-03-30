/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

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

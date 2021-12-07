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

package com.github.drbookings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CleaningExpensesFactory {

    public static List<CleaningExpense> build(final Collection<? extends CleaningEntry> cleanings,
	    final boolean includeBlack) {
	final List<CleaningExpense> result = new ArrayList<>();
	for (final CleaningEntry ce : cleanings) {

	    if (!includeBlack && ce.isBlack()) {
		// skip
	    } else {
		add(result, ce);
	    }
	}
	return result;
    }

    private static void add(final List<CleaningExpense> result, final CleaningEntry ce) {
	final CleaningExpense ce2 = new CleaningExpense(ce);
	ce2.setOrigin(ce.getBooking() != null ? ce.getBooking().getBookingOrigin() : null);
	result.add(ce2);
    }

}

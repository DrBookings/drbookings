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

package com.github.drbookings.model.data.ser;

import com.github.drbookings.CleaningExpense;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CleaningExpenseSerBuilder {

    public static List<CleaningExpenseSer> build(final Collection<? extends CleaningExpense> expenses) {
	final List<CleaningExpenseSer> result = new ArrayList<>(expenses.size());
	for (final CleaningExpense p : expenses) {
	    result.add(build(p));
	}

	return result;
    }

    public static CleaningExpenseSer build(final CleaningExpense expense) {
	final CleaningExpenseSer result = new CleaningExpenseSer();
	result.amount = expense.getAmount().getNumber().toString();
	result.date = expense.getDate();
	result.cleaningId = expense.getCleaningEntry().getId();
	return result;
    }

}

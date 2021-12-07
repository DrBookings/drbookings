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

package com.github.drbookings.ser;

import com.github.drbookings.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimpleDataStoreCore implements DataStoreCore {

    private List<BookingBean> bookings = new ArrayList<>();

    private List<CleaningEntry> cleanings = new ArrayList<>();

    private List<ExpenseBean> expenses = new ArrayList<>();

    @Override
    public List<BookingBean> getBookings() {
	return bookings;
    }

    @Override
    public List<CleaningEntry> getCleanings() {
	return cleanings;
    }

    @Override
    public List<ExpenseBean> getExpenses() {
	return expenses;
    }

    @Override
    public List<ExpenseBean> getExpensesIncludingCleaning(final boolean includeBlack) {
	final List<ExpenseBean> result = new ArrayList<>(expenses);
	final List<CleaningExpense> cleanings = CleaningExpensesFactory.build(getCleanings(), includeBlack);
	result.addAll(cleanings);
	return result;
    }

    public DataStoreCore setBookings(final Collection<? extends BookingBean> elements) {
	this.bookings = new ArrayList<>(elements);
	return this;
    }

    public DataStoreCore setCleanings(final Collection<? extends CleaningEntry> elements) {
	this.cleanings = new ArrayList<>(elements);
	return this;
    }

    public DataStoreCore setExpenses(final Collection<? extends ExpenseBean> elements) {
	this.expenses = new ArrayList<>(elements);
	return this;
    }

}

package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

public class DrBookingsExpensesData {

    protected final Map<LocalDate, Expense> entries;

    public DrBookingsExpensesData() {
	this.entries = new TreeMap<>();
    }

    public void addExpense(final Expense entry) {
	this.entries.put(entry.getDate(), entry);
    }

    public Expense getExpense(final LocalDate date) {
	return entries.get(date);
    }

}

package com.github.drbookings.model.data.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.model.data.CleaningExpense;

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
	result.amount = expense.getAmount().getNumber().floatValue();
	result.date = expense.getDate();
	result.cleaningId = expense.getCleaningEntry().getId();
	return result;
    }

}

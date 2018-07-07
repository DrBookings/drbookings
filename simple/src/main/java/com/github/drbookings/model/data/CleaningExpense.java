package com.github.drbookings.model.data;

import java.util.ArrayList;
import java.util.List;

import com.github.drbookings.ui.CleaningEntry;

public class CleaningExpense extends Expense {

    public static List<CleaningExpense> build(final List<CleaningEntry> cleanings) {
	final List<CleaningExpense> result = new ArrayList<>();
	for (final CleaningEntry ce : cleanings) {
	    result.add(new CleaningExpense(ce));
	}
	return result;
    }

    private final CleaningEntry cleaningEntry;

    public CleaningExpense(final CleaningEntry ce) {
	super(ce.getName(), ce.getDate(), ce.getCleaningCosts());
	this.cleaningEntry = ce;
    }

    public CleaningEntry getCleaningEntry() {
	return cleaningEntry;
    }

}

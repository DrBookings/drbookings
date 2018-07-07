package com.github.drbookings.model.data.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.model.data.Expense;

public class ExpenseSerBuilder {

    public static List<ExpenseSer> build(final Collection<? extends Expense> expenses) {
        final List<ExpenseSer> result = new ArrayList<>(expenses.size());
        for (final Expense p : expenses) {
            result.add(ExpenseSerBuilder.build(p));
        }
    
        return result;
    }

    public static ExpenseSer build(final Expense expense) {
        final ExpenseSer result = new ExpenseSer();
        result.amount = expense.getAmount().getNumber().floatValue();
        result.date = expense.getDate();
        return result;
    }

}

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

import com.google.common.collect.Range;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Calculates the net earnings. That is, the net income minus
 * <ol>
 * <li>cleaning costs</li>
 * <li>fixed costs (anteilig origin)</li>
 * <li>dynamic costs (anteilig origin)</li>
 * </ol>
 *
 * @see DefaultNetIncomeSupplier
 *
 * @author Alexander Kerner
 * @date 2018-09-18
 *
 */
public class DefaultNetEarningsSupplier extends PaymentDateFilter4 {

    private final Collection<? extends CleaningEntry> cleanings;
    private final Collection<? extends ExpenseBean> commonExpenses;

    public DefaultNetEarningsSupplier(final Collection<? extends CleaningEntry> cleanings,
	    final Collection<? extends ExpenseBean> commonExpenses) {

	super();
	this.cleanings = cleanings;
	this.commonExpenses = commonExpenses;
    }

    public DefaultNetEarningsSupplier(final Range<LocalDate> dates, final Collection<? extends CleaningEntry> cleanings,
	    final Collection<? extends ExpenseBean> commonExpenses) {

	super(dates);
	this.cleanings = cleanings;
	this.commonExpenses = commonExpenses;
    }

    public DefaultNetEarningsSupplier(final YearMonth month, final Collection<? extends CleaningEntry> cleanings,
	    final Collection<? extends ExpenseBean> commonExpenses) {

	super(month);
	this.cleanings = cleanings;
	this.commonExpenses = commonExpenses;
    }

    public MonetaryAmount apply(final Collection<? extends BookingBean> bookings, final BookingOrigin origin,
	    final boolean cheat) {

	final MonetaryAmount netIncome = new DefaultNetIncomeSupplier(getDateRange()).apply(bookings);
	final MonetaryAmount fixedCostsAnteilig = new DefaultFixedCostsAnteilCalculator(getDateRange(), bookings)
		.getFixkostenAnteil(origin, cheat, Payments.getSum(commonExpenses));
	// all white cleaning costs that have been payed for this origin
	final MonetaryAmount cleaningCosts = Payments.getSum(CleaningExpensesFactory.build(cleanings, false).stream()
		.filter(e -> (e.getOrigin() != null) && e.getOrigin().equals(origin)).collect(Collectors.toList()));
	final MonetaryAmount result = netIncome.subtract(fixedCostsAnteilig).subtract(cleaningCosts);
	return result;
    }
}

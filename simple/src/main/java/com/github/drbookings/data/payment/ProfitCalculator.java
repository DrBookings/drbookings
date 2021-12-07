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

package com.github.drbookings.data.payment;

import com.github.drbookings.*;
import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

public class ProfitCalculator extends SimpleDateRangeHandler {

    public static final float DEFAULT_PAYOUT_FACTOR = 1f;

    private static final Logger logger = LoggerFactory.getLogger(ProfitCalculator.class);

    private final Collection<? extends BookingBean> bookings;

    private final Collection<? extends ExpenseBean> expenses;

    private final float payoutFactor;

    public ProfitCalculator(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends ExpenseBean> expenses) {
	this(dates, bookings, expenses, DEFAULT_PAYOUT_FACTOR);
    }

    public ProfitCalculator(final Range<LocalDate> dates, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends ExpenseBean> expenses, final float payoutFactor) {
	super(dates);
	this.bookings = bookings;
	this.expenses = expenses;
	this.payoutFactor = payoutFactor;
    }

    public ProfitCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends ExpenseBean> variableExpenses) {
	this(month, bookings, variableExpenses, DEFAULT_PAYOUT_FACTOR);
    }

    public ProfitCalculator(final YearMonth month, final Collection<? extends BookingBean> bookings,
	    final Collection<? extends ExpenseBean> variableExpenses, final float payoutFactor) {
	super(month);
	this.bookings = bookings;
	this.expenses = variableExpenses;
	this.payoutFactor = payoutFactor;
    }

    Collection<? extends BookingBean> getBookings() {
	return bookings;
    }

    private long getNumberOfMonths() {
	final long months = LocalDates.getNumberOfMonth(getDateRange());
	return months;
    }

    public MonetaryAmount getPayout(final BookingOrigin origin, final boolean cheat) {

	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(getBookings());

	final MonetaryAmount thisBookingsPaymentSum = Payments.getSum(bbo.getByOrigin(origin), getDateRange());

	final MonetaryAmount commonExpenses = Payments.getSum(Expenses.getCommonExpenses(expenses));

	final MonetaryAmount assignedExpenses = Payments.getSum(Expenses.getAssignedExpenses(expenses, origin));

	final MonetaryAmount fixCostenAnteil = getFixkostenAnteil(origin, cheat, commonExpenses);

	final MonetaryAmount result1 = thisBookingsPaymentSum.subtract(fixCostenAnteil);

	final MonetaryAmount result2 = result1.subtract(assignedExpenses);

	final MonetaryAmount result3 = applyPayoutFactor(result2, getPayoutFactor());

	return result3;
    }

    /**
     * Applies the payout factor. Note that a negative amount will be increased by a
     * smaller payout factor.
     *
     * @param amount
     *            the amount to which the payout factor should be applied
     * @param payoutFactor
     *            the payout factor to apply
     * @return the adjusted payout
     */
    public static MonetaryAmount applyPayoutFactor(final MonetaryAmount amount, final float payoutFactor) {
	if (payoutFactor > 1) {
	    if (logger.isWarnEnabled()) {
		logger.warn("Payout above 100%");
	    }
	}
	final MonetaryAmount result = amount.multiply(payoutFactor);
	return result;
    }

    public static MonetaryAmount applyPayoutFactor(final String amount, final float payoutFactor) {
	return applyPayoutFactor(Payments.createMondary(amount), payoutFactor);
    }

    private MonetaryAmount getFixkostenAnteil(final BookingOrigin origin, final boolean cheat,
	    final MonetaryAmount additionalCostsAllRooms) {
	final double percentageThisBookings = getPercentageThisBookings(origin, cheat);
	final MonetaryAmount result = additionalCostsAllRooms.multiply(percentageThisBookings);
	return result;
    }

    static double getPercentageThisBookings(final long thisBookingsCount, final long allBookingsCount) {
	return (double) thisBookingsCount / allBookingsCount;
    }

    private double getPercentageThisBookings(final BookingOrigin origin, final boolean cheat) {
	if (cheat && !ByOriginFilter.NON_CHEAT_FILTER_BOOKING_ORIGIN.test(origin)) {
	    return 0d;
	}
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(getBookings());
	final long allBookings = new NumberOfNightsCounter(getDateRange(), bbo.getAllBookings(cheat)).countNights();
	final long thisBookings = new NumberOfNightsCounter(getDateRange(), bbo.getByOrigin(origin)).countNights();
	final double result = getPercentageThisBookings(thisBookings, allBookings);
	if (logger.isDebugEnabled()) {
	    logger.debug("Percent bookings for " + origin + ": " + String.format("%6.4f", result));
	}
	return result;
    }

    public MonetaryAmount getPayout(final String originName, final boolean cheat) {
	return getPayout(new BookingOrigin(originName), cheat);
    }

    float getPayoutFactor() {
	return payoutFactor;
    }

}

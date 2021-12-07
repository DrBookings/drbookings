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
import org.javamoney.moneta.Money;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

public class Payments {

    public static final String DEFAULT_CURRENCY_STRING = "EUR";
    public static final Currency DEFAULT_CURRENCY = Currency.getInstance(DEFAULT_CURRENCY_STRING);
    public static final CurrencyUnit DEFAULT_CURRENCY_UNIT = Monetary.getCurrency(DEFAULT_CURRENCY.getCurrencyCode());

    public static MonetaryAmount createMondary(final float amount) {

	return createMondary(Float.toString(amount));
    }

    public static MonetaryAmount createMondary(final double amount) {

	final MonetaryAmount result = Money.of(amount, Monetary.getCurrency(DEFAULT_CURRENCY.getCurrencyCode()));
	// System.err.println(amount);
	// System.err.println(result);
	// System.err.println();
	return result;
    }

    public static MonetaryAmount createMondary(final String amount) {

	return createMondary(DEFAULT_CURRENCY, amount);
    }

    public static MonetaryAmount createMondary(final Currency currency, final String amount) {

	final MonetaryAmount result = Money.of(new BigDecimal(amount),
		Monetary.getCurrency(currency.getCurrencyCode()));
	// System.err.println(amount);
	// System.err.println(result);
	// System.err.println();
	return result;
    }

    public static Range<LocalDate> getDateRange(final Collection<? extends Payment> payments) {

	if (payments == null)
	    throw new NullPointerException();
	if (payments.isEmpty())
	    throw new IllegalArgumentException();
	final LocalDate min = payments.stream().map(Payment::getDate).min(LocalDate::compareTo).get();
	final LocalDate max = payments.stream().map(Payment::getDate).max(LocalDate::compareTo).get();
	return Range.closed(min, max);
    }

    public static final MathContext getDefaultMathContext() {

	final MathContext mathContext = new MathContext(Numbers.DEFAULT_SCALE, Numbers.DEFAULT_ROUNDING_MODE);
	return mathContext;
    }

    public static Optional<Payment> getLastPayment(final Collection<? extends Payment> payments) {

	Payment result = null;
	for (final Payment p : payments) {
	    if ((result == null) || result.getDate().isBefore(p.getDate())) {
		result = p;
	    }
	}
	return Optional.ofNullable(result);
    }

    public static List<Payment> getPaymentsInRange(final Range<LocalDate> dates,
	    final Collection<? extends Payment> payments) {

	Objects.requireNonNull(dates);
	Objects.requireNonNull(payments);
	final List<Payment> result = new ArrayList<>();
	for (final Payment p : payments) {
	    if (p == null)
		throw new NullPointerException();
	    if (p.getDate().isBefore(dates.upperEndpoint().plusDays(1))
		    && p.getDate().isAfter(dates.lowerEndpoint().minusDays(1))) {
		result.add(p);
	    }
	}
	return result;
    }

    public static MonetaryAmount getSum(final Collection<? extends Payment> payments) {

	MonetaryAmount sum = createMondary(0);
	for (final Payment p : payments) {
	    sum = sum.add(p.getAmount());
	    // System.err.println(p.getAmount());
	}
	return sum;
    }

    public static MonetaryAmount getSum(final Collection<? extends PaymentProvider> bookings,
	    final Range<LocalDate> dateRange) {

	final List<Payment> bookingPayments = new PaymentsCollector(dateRange).collect(bookings);
	return getSum(bookingPayments);
    }

    public static MonetaryAmount getSum(final Collection<? extends PaymentProvider> bookings, final YearMonth month) {

	return getSum(bookings, LocalDates.toDateRange(month));
    }
}

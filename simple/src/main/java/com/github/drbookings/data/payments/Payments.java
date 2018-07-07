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

package com.github.drbookings.data.payments;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.github.drbookings.data.PaymentProvider;
import com.github.drbookings.model.Payment;
import com.google.common.collect.Range;

public class Payments {

    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("EUR");

    public static MonetaryAmount createMondary(final Currency currency, final double amount) {
	return Monetary.getDefaultAmountFactory().setCurrency(currency.getCurrencyCode()).setNumber(amount).create();
    }

    public static MonetaryAmount createMondary(final double amount) {
	return createMondary(DEFAULT_CURRENCY, amount);
    }

    public static Range<LocalDate> getDateRange(final Collection<? extends Payment> payments) {
	if (payments == null) {
	    throw new NullPointerException();
	}
	if (payments.isEmpty()) {
	    throw new IllegalArgumentException();
	}
	final LocalDate min = payments.stream().map(Payment::getDate).min(LocalDate::compareTo).get();
	final LocalDate max = payments.stream().map(Payment::getDate).max(LocalDate::compareTo).get();
	return Range.closed(min, max);
    }

    public static Optional<Payment> getLastPayment(final Collection<? extends Payment> payments) {
	Payment result = null;
	for (final Payment p : payments) {
	    if (result == null || result.getDate().isBefore(p.getDate())) {
		result = p;
	    }
	}
	return Optional.ofNullable(result);
    }

    public static List<Payment> getPaymentInRange(final Range<LocalDate> dates,
	    final Collection<? extends Payment> payments) {
	final List<Payment> result = payments.stream()
		.filter(p -> (p.getDate().isBefore(dates.upperEndpoint().plusDays(1))
			&& p.getDate().isAfter(dates.lowerEndpoint().minusDays(1))))
		.collect(Collectors.toList());
	return result;
    }

    public static MonetaryAmount getSum(final Collection<? extends Payment> payments) {
	MonetaryAmount sum = Payments.createMondary(0);
	for (final Payment p : payments) {
	    sum = sum.add(p.getAmount());
	}
	return sum;
    }

    public static MonetaryAmount getSum(final Collection<? extends PaymentProvider> bookings,
	    final Range<LocalDate> dateRange) {
	final List<Payment> bookingPayments = new PaymentsCollector(dateRange).collect(bookings);
	return getSum(bookingPayments);
    }
}

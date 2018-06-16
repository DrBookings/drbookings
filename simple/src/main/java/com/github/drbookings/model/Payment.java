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

package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import com.github.drbookings.model.ser.PaymentSer;

public class Payment {

    public static final Currency DEFAULT_CURRENCY = Currency.getInstance("EUR");

    private static MonetaryAmount createMondary(final Currency currency, final double amount) {
	return Monetary.getDefaultAmountFactory().setCurrency(currency.getCurrencyCode()).setNumber(amount).create();
    }

    public static List<Payment> transform(final List<? extends PaymentSer> paymentsSoFar) {
	final List<Payment> result = new ArrayList<>();
	for (final PaymentSer ps : paymentsSoFar) {
	    result.add(new Payment(ps.date, ps.amount));
	}
	return result;
    }

    private final Currency currency;

    private final LocalDate date;

    private final MonetaryAmount amount;

    public Payment(final Currency currency, final LocalDate date, final MonetaryAmount amount) {
	this.currency = currency;
	this.date = date;
	this.amount = amount;
    }

    public Payment(final LocalDate date, final double amount) {
	this(DEFAULT_CURRENCY, date, createMondary(DEFAULT_CURRENCY, amount));
    }

    public Payment(final LocalDate date, final MonetaryAmount amount) {
	this(DEFAULT_CURRENCY, date, amount);
    }

    public Payment(final LocalDate date, final String amount) {
	this(date, Double.parseDouble(amount));
    }

    public MonetaryAmount getAmount() {
	return amount;
    }

    public Currency getCurrency() {
	return currency;
    }

    public LocalDate getDate() {
	return date;
    }

    @Override
    public String toString() {
	return "Payment{" + "date=" + date + ", amount=" + amount + '}';
    }
}

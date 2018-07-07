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

import javax.money.MonetaryAmount;

import com.github.drbookings.data.payments.Payments;
import com.github.drbookings.model.data.ser.PaymentSer;

/**
 *
 * @author Alexander Kerner
 *
 */
public class PaymentImpl implements Payment {

    public static List<PaymentImpl> build(final List<? extends PaymentSer> paymentsSoFar) {
	final List<PaymentImpl> result = new ArrayList<>();
	for (final PaymentSer ps : paymentsSoFar) {
	    result.add(new PaymentImpl(ps.date, ps.amount));
	}
	return result;
    }

    private final MonetaryAmount amount;

    private final Currency currency;

    private final LocalDate date;

    public PaymentImpl(final LocalDate date, final MonetaryAmount amount, final Currency currency) {
	this.currency = currency;
	this.date = date;
	this.amount = amount;
    }

    public PaymentImpl(final LocalDate date, final double amount) {
	this(date, Payments.createMondary(Payments.DEFAULT_CURRENCY, amount), Payments.DEFAULT_CURRENCY);
    }

    public PaymentImpl(final LocalDate date, final MonetaryAmount amount) {
	this(date, amount, Payments.DEFAULT_CURRENCY);
    }

    public PaymentImpl(final LocalDate date, final String amount) {
	this(date, Double.parseDouble(amount));
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof PaymentImpl)) {
	    return false;
	}
	final PaymentImpl other = (PaymentImpl) obj;
	if (amount == null) {
	    if (other.amount != null) {
		return false;
	    }
	} else if (!amount.equals(other.amount)) {
	    return false;
	}
	if (currency == null) {
	    if (other.currency != null) {
		return false;
	    }
	} else if (!currency.equals(other.currency)) {
	    return false;
	}
	if (date == null) {
	    if (other.date != null) {
		return false;
	    }
	} else if (!date.equals(other.date)) {
	    return false;
	}
	return true;
    }

    /* (non-Javadoc)
     * @see com.github.drbookings.model.Payment#getAmount()
     */
    @Override
    public MonetaryAmount getAmount() {
	return amount;
    }

    /* (non-Javadoc)
     * @see com.github.drbookings.model.Payment#getCurrency()
     */
    @Override
    public Currency getCurrency() {
	return currency;
    }

    /* (non-Javadoc)
     * @see com.github.drbookings.model.Payment#getDate()
     */
    @Override
    public LocalDate getDate() {
	return date;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (amount == null ? 0 : amount.hashCode());
	result = prime * result + (currency == null ? 0 : currency.hashCode());
	result = prime * result + (date == null ? 0 : date.hashCode());
	return result;
    }

    @Override
    public String toString() {
	return "Payment{" + "date=" + date + ", amount=" + String.format("%6.2f", amount.getNumber().doubleValue())
		+ '}';
    }
}

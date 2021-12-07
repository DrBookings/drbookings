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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;

public abstract class AbstractMonetaryAmountFromBookingsSupplier extends PaymentDateFilter4
	implements AbstractMonetaryAmountSupplier<BookingBean>, MonetaryAmountFromBookingsSupplier {

    private final static Logger logger = LoggerFactory.getLogger(AbstractMonetaryAmountFromBookingsSupplier.class);

    public AbstractMonetaryAmountFromBookingsSupplier(final Range<LocalDate> dates) {

	super(dates);
    }

    public AbstractMonetaryAmountFromBookingsSupplier(final YearMonth month) {

	super(month);
    }

    public AbstractMonetaryAmountFromBookingsSupplier() {

	super();
    }

    @Override
    public MonetaryAmount apply(final Collection<? extends BookingBean> elements) {
	return AbstractMonetaryAmountSupplier.super.apply(elements);
    }

    @Override
    public MonetaryAmount apply(final BookingBean booking) {

	if (bookingInRange(booking))
	    return applyInDateRange(booking);
	else {
	    if (logger.isDebugEnabled()) {
		logger.debug("Skip " + booking);
	    }
	}
	return Money.of(0, Payments.DEFAULT_CURRENCY_STRING);
    }

    protected abstract MonetaryAmount applyInDateRange(BookingBean booking);
}

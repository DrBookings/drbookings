/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.DrBookingsApplication;
import com.github.drbookings.model.data.BookingBean;
import com.google.common.collect.Range;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.MonetaryAmountFactory;
import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.stream.Collectors;

public class BookingEarningsCalculator extends EarningsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(BookingEarningsCalculator.class);


    public BookingEarningsCalculator filterToDateRange(Range<LocalDate> dateRange) {
        super.filterToDateRange(dateRange);
        return this;
    }

    public BookingEarningsCalculator filterForPaymentDone(boolean paymentDone) {
        super.filterForPaymentDone(paymentDone);
        return this;
    }

    public BookingEarningsCalculator filterForNetEarnings(boolean netEarnigns) {
        super.filterForNetEarnings(netEarnigns);
        return this;
    }


    public float calculateEarnings(Collection<? extends BookingBean> bookings) {
        if (isPaymentDone()) {
            bookings = bookings.stream().filter(b -> b.isPaymentDone()).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        if (getDateRange() != null) {
            bookings = bookings.stream().filter(b -> getDateRange().contains(b.getCheckOut())).collect(Collectors.toCollection(LinkedHashSet::new));
        }
        MonetaryAmountFactory<?> moneyFactory = Monetary.getDefaultAmountFactory().setCurrency(DrBookingsApplication.DEFAULT_CURRENCY.getCurrencyCode());
        MonetaryAmount result = moneyFactory.setNumber(0).create();
        for (BookingBean b : bookings) {
            result = result.add(moneyFactory.setNumber(b.getEarnings(isNetEarnings())).create());
        }

        return result.getNumber().floatValue();
    }


}
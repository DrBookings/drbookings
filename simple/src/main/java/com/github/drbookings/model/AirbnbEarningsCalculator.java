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

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.BookingMapFactory;
import com.github.drbookings.ui.BookingEntry;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class AirbnbEarningsCalculator extends EarningsCalculator {

    private static final Logger logger = LoggerFactory.getLogger(AirbnbEarningsCalculator.class);

    public AirbnbEarningsCalculator filterForPaymentDone(boolean paymentDone) {
        super.filterForPaymentDone(paymentDone);
        return this;
    }

    public AirbnbEarningsCalculator filterForNetEarnings(boolean netEarnigns) {
        super.filterForNetEarnings(netEarnigns);
        return this;
    }

    public float calculateEarnings(Collection<? extends BookingEntry> bookings) {

        double result = 0;

        Multimap<Booking, BookingEntry> bookingMap = BookingMapFactory.buildMap(bookings);

        long nightsToPay = getNightsToPay();

        for (Map.Entry<Booking, Collection<BookingEntry>> e : bookingMap.asMap().entrySet()) {
            long nightsBooked = e.getKey().getNumberOfNights();
            if (nightsBooked > nightsToPay) {
                double nightly = getNightly(e.getKey());
                double localResult = nightly * nightsToPay;
                result += localResult;
                if(logger.isDebugEnabled()){
                    logger.debug("Partial payment for " + e.getKey() + " of " + localResult + "/" + e.getKey().getEarnings(isNetEarnings()));
                }
            } else {
                result += super.calculateEarnings(e.getValue());
            }

        }


        return (float) result;

    }

    private float getNightly(Booking booking) {
        return booking.getNetEarnings() / booking.getNumberOfNights();
    }

    private int getNightsToPay() {
        return 31;
    }

}
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

import com.github.drbookings.ser.BookingBeanSer;

import java.time.LocalDate;

public class PayoutDateProvider {
    /**
     * For Airbnb, the payment is received usually two days after check-in.
     */
    public static final int DEFAULT_PAYOUT_OFFSET = 1;

    private int payoutOffset = DEFAULT_PAYOUT_OFFSET;

    public LocalDate getPayoutDate(final BookingBean bbb) {

	if (!bbb.getPayments().isEmpty())
	    throw new IllegalArgumentException("Get payment dates from payments");
	else {
	    // backwards compatible
	    if (bbb.getDateOfPayment() != null)
		return bbb.getDateOfPayment();
	    // if no data is available
	    if (bbb.getBookingOrigin().getName().equalsIgnoreCase("booking"))
		return bbb.getCheckOut();
	    else
		return bbb.getCheckIn().plusDays(getPayoutOffset());
	}
    }

    public LocalDate getPayoutDate(final BookingBeanSer bbb) {

	if (!bbb.paymentsSoFar.isEmpty())
	    throw new IllegalArgumentException("Get payment dates from payments");
	else {
	    // backwards compatible
	    if (bbb.dateOfPayment != null)
		return bbb.dateOfPayment;
	    // if no data is available
	    if (bbb.source.equalsIgnoreCase("booking"))
		return bbb.checkOutDate;
	    else
		return bbb.checkInDate.plusDays(getPayoutOffset());
	}
    }

    public int getPayoutOffset() {
	return payoutOffset;
    }

    public PayoutDateProvider setPayoutOffset(final int payoutOffset) {
	this.payoutOffset = payoutOffset;
	return this;
    }

}

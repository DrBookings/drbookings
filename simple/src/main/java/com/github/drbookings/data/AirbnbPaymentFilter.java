package com.github.drbookings.data;

import java.time.YearMonth;

/**
 * Airbnb pays bookings a few hours after check-in.
 *
 * @author Alexander Kerner
 *
 */
public class AirbnbPaymentFilter extends SimplePaymentFilter {

    public AirbnbPaymentFilter() {
	super();

    }

    public AirbnbPaymentFilter(final YearMonth month) {
	super(month);

    }

}

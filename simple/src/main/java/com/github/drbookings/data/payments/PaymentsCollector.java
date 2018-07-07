package com.github.drbookings.data.payments;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.data.DateRangeHandler;
import com.github.drbookings.data.PaymentProvider;
import com.github.drbookings.model.Payment;
import com.github.drbookings.model.PaymentImpl;
import com.github.drbookings.model.data.BookingBean;
import com.google.common.collect.Range;

public class PaymentsCollector extends DateRangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentsCollector.class);

    public PaymentsCollector(final Range<LocalDate> dates) {
	super(dates);

    }

    public PaymentsCollector(final YearMonth of) {
	super(of);

    }

    public List<Payment> collect(final Collection<? extends PaymentProvider> elements) {
	final List<Payment> result = new ArrayList<>();
	for (final PaymentProvider bb : elements) {
	    if (bb.getPayments().isEmpty()) {
		if (bb instanceof BookingBean) {
		    final BookingBean bbb = (BookingBean) bb;
		    final float payment = bbb.getGrossEarnings();
		    if (bbb.isPaymentDone() && payment != 0) {
			final LocalDate date;
			if (bbb.getBookingOrigin().getName().equalsIgnoreCase("airbnb")) {
			    date = bbb.getCheckIn();
			} else {
			    date = bbb.getCheckOut();
			}

			final Payment p = new PaymentImpl(date, payment);
			if (logger.isInfoEnabled()) {
			    logger.info(
				    "No payments found for " + bbb + ", creating one from gross earnings (" + p + ")");
			}
			result.add(p);
		    }
		}
	    }
	    for (final Payment p : bb.getPayments()) {
		if (getDates().contains(p.getDate())) {
		    result.add(p);
		}
	    }
	}
	return result;
    }
}

package com.github.drbookings.data.payments;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.model.Payment;
import com.github.drbookings.model.data.BookingBean;
import com.google.common.collect.Range;

public class PaymentsCollector {

    private final Range<LocalDate> dates;

    public PaymentsCollector(final Range<LocalDate> dates) {
	this.dates = dates;
    }

    public PaymentsCollector(final YearMonth of) {
	this.dates = Range.closed(of.atDay(01), of.atEndOfMonth());
    }

    public List<Payment> collect(final Collection<? extends BookingBean> bookings) {

    }
}

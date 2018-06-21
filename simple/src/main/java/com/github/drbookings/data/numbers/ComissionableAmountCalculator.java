package com.github.drbookings.data.numbers;

import java.math.BigDecimal;

import com.github.drbookings.model.data.BookingBean;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-21
 *
 */
public class ComissionableAmountCalculator implements NumberSupplier {

    @Override
    public BigDecimal apply(final BookingBean booking) {
	final BigDecimal gross = BigDecimal.valueOf(booking.getGrossEarnings());
	final BigDecimal cleaning = BigDecimal.valueOf(booking.getCleaningFees());
	final BigDecimal grossMinusCleaning = gross.subtract(cleaning);
	return grossMinusCleaning;
    }

}

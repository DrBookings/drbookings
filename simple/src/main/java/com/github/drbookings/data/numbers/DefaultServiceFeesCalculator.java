package com.github.drbookings.data.numbers;

import java.math.BigDecimal;

import com.github.drbookings.model.data.BookingBean;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-21
 *
 */
public class DefaultServiceFeesCalculator implements NumberSupplier {

    @Override
    public BigDecimal apply(final BookingBean booking) {
	final BigDecimal gross = BigDecimal.valueOf(booking.getGrossEarnings());
	final BigDecimal cleaning = BigDecimal.valueOf(booking.getCleaningFees());
	final BigDecimal servicesFeesPercent = BigDecimal.valueOf(booking.getServiceFeesPercent());
	final BigDecimal grossMinusCleaning = gross.subtract(cleaning);
	final BigDecimal result = grossMinusCleaning.multiply(servicesFeesPercent.divide(BigDecimal.valueOf(100)));
	return result;
    }

}

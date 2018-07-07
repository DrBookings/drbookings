package com.github.drbookings.data.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.data.BookingBean;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-21
 *
 */
public interface NumberSupplier {

    BigDecimal apply(BookingBean booking);

    default BigDecimal apply(final BookingEntry booking) {
	final double numberOfNights = booking.getElement().getNumberOfNights();
	return apply(booking.getElement()).divide(BigDecimal.valueOf(numberOfNights), getScale(), getRoundingMode());
    }

    default RoundingMode getRoundingMode() {
	return Numbers.DEFAULT_ROUNDING_MODE;
    }

    default int getScale() {
	return Numbers.DEFAULT_SCALE;
    }

}
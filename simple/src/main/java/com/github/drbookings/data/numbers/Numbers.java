package com.github.drbookings.data.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Numbers {

    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_EVEN;

    public static final int DEFAULT_SCALE = 5;

    public static BigDecimal getDefault(final double number) {
	return BigDecimal.valueOf(number).setScale(DEFAULT_SCALE, DEFAULT_ROUNDING_MODE);
    }

}

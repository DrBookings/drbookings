package com.github.drbookings.model;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAccessor;

public class TemporalQueries {

    public static boolean isCurrentMonth(final TemporalAccessor temporal) {
	final LocalDate ref = LocalDate.now();
	return Month.from(temporal) == Month.from(ref) && Year.from(temporal).equals(Year.from(ref));
    }

    public static boolean isPreviousMonth(final TemporalAccessor temporal) {
	final LocalDate ref = LocalDate.now().minusMonths(1);
	return Month.from(temporal) == Month.from(ref) && Year.from(temporal).equals(Year.from(ref));
    }
}

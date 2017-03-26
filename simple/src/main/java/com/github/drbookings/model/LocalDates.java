package com.github.drbookings.model;

import java.time.LocalDate;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class LocalDates {

    public static Pair<LocalDate, LocalDate> getFirstAndLastDayOfMonth(final int month) {
	final int year = LocalDate.now().getYear();
	final LocalDate firstDay = LocalDate.of(year, month, 1);
	final LocalDate lastDay = LocalDate.of(year, month,
		firstDay.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).getDayOfMonth());
	return new ImmutablePair<>(firstDay, lastDay);
    }

}

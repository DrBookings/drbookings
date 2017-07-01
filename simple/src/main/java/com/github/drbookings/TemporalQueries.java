package com.github.drbookings;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAccessor;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TemporalQueries {

	public static boolean isCurrentMonth(final TemporalAccessor temporal) {
		final LocalDate ref = LocalDate.now();
		return Month.from(temporal) == Month.from(ref) && Year.from(temporal).equals(Year.from(ref));
	}

	public static boolean isPreviousMonth(final TemporalAccessor temporal) {
		final LocalDate ref = LocalDate.now().minusMonths(1);
		return Month.from(temporal) == Month.from(ref) && Year.from(temporal).equals(Year.from(ref));
	}

	public static boolean isPreviousMonthOrEarlier(final TemporalAccessor temporal) {
		final LocalDate ref = LocalDate.now().minusMonths(1);
		return Month.from(temporal).compareTo(Month.from(ref)) <= 0 && Year.from(temporal).equals(Year.from(ref));
	}

	public static int countMonths(final Collection<LocalDate> allDates) {
		final Set<YearMonth> months = allDates.stream().map(l -> YearMonth.from(l)).collect(Collectors.toSet());
		return months.size();

	}

	public static long countOccurrences(final Collection<? extends LocalDate> allDates, final int dayOfMonth) {
		long result = 0;
		for (final LocalDate d : allDates) {
			if (d.getDayOfMonth() == dayOfMonth) {
				result++;
			}
		}
		return result;
	}
}

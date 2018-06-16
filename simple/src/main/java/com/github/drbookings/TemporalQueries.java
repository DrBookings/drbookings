/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class TemporalQueries {

    public static int countMonths(final Collection<LocalDate> allDates) {
	final Set<YearMonth> months = allDates.stream().map(l -> YearMonth.from(l)).collect(Collectors.toSet());
	return months.size();
    }

    public static int countMonths(final LocalDate... allDates) {
	return countMonths(Arrays.asList(allDates));
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

    public static boolean isCurrentMonth(final TemporalAccessor temporal) {
	final LocalDate ref = LocalDate.now();
	return (Month.from(temporal) == Month.from(ref)) && Year.from(temporal).equals(Year.from(ref));
    }

    public static boolean isPreviousMonth(final TemporalAccessor temporal) {
	final LocalDate ref = LocalDate.now().minusMonths(1);
	return (Month.from(temporal) == Month.from(ref)) && Year.from(temporal).equals(Year.from(ref));
    }

    public static boolean isPreviousMonthOrEarlier(final TemporalAccessor temporal) {
	final LocalDate ref = LocalDate.now().minusMonths(1);
	return (Month.from(temporal).compareTo(Month.from(ref)) <= 0) && Year.from(temporal).equals(Year.from(ref));
    }
}

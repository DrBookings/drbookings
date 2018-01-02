/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.google.common.collect.Range;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.TreeSet;

public class LocalDates {

	public static String getDateString(final LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("EE, dd MMM"));
	}

	public static Range<LocalDate> getDateRange(final Collection<? extends LocalDate> dates) {
        return getDateRange(dates, false);
    }

    public static Range<LocalDate> getDateRange(final Collection<? extends LocalDate> dates, final boolean
            oneMoreAtTheBeginning) {
        final TreeSet<LocalDate> set = new TreeSet<>(dates);
        if (set.isEmpty()) {
            return null;
        }
        final LocalDate d1;
        final LocalDate d2;
        d2 = set.last();
        if (oneMoreAtTheBeginning) {
            d1 = set.first().minusDays(1);
        } else {
            d1 = set.first();
        }
        return Range.closed(d1, d2);
    }

	public static TextFlow getDateText(final LocalDate date) {
		final TextFlow tf = new TextFlow();
		final Text t0 = new Text(getDateString(date));
		tf.getChildren().addAll(t0);
		return tf;
	}

	public static Pair<LocalDate, LocalDate> getFirstAndLastDayOfMonth(final int month) {
		final int year = LocalDate.now().getYear();
		final LocalDate firstDay = LocalDate.of(year, month, 1);
		final LocalDate lastDay = getLastDayOfMonth(firstDay);
		return new ImmutablePair<>(firstDay, lastDay);
	}

	public static LocalDate getLastDayOfMonth(final LocalDate date) {
		return date.with(TemporalAdjusters.lastDayOfMonth());
	}

	public static long getNumberOfDays(final LocalDate date1, final LocalDate date2) {
		final long daysElapsed = ChronoUnit.DAYS.between(date1, date2);
		return daysElapsed + 1;
	}

	public static long getNumberOfNights(final LocalDate date1, final LocalDate date2) {
		final long daysElapsed = getNumberOfDays(date1, date2);
		return daysElapsed - 1;
	}

	public static String getYearString(final LocalDate date) {
		return date.format(DateTimeFormatter.ofPattern("yyyy"));
	}

	public static TextFlow getYearText(final LocalDate date) {
		final TextFlow tf = new TextFlow();
		final Text t0 = new Text(getYearString(date));
		t0.getStyleClass().add("center-text");
		tf.getChildren().addAll(t0);
		return tf;
	}

	public static boolean isCurrentMonth(final LocalDate date) {
		final Month month = LocalDate.now().getMonth();
		return date.getMonth().equals(month) && date.getYear() == LocalDate.now().getYear();
	}

	public static boolean isLastDayOfMonth(final LocalDate date) {
		return date.equals(getLastDayOfMonth(date));
	}

	public static boolean isLastMonth(final LocalDate date) {
		final Month month = LocalDate.now().getMonth().minus(1);
        return date.getMonth().equals(month) && (date.getYear() == LocalDate.now().getYear() || date.getYear() == LocalDate.now().getYear() - 1);
	}

	public static boolean isLastThreeMonths(final LocalDate date) {
		final Month month = LocalDate.now().getMonth().minus(1);
        if (date.getMonth().equals(month) && (date.getYear() == LocalDate.now().getYear() || date.getYear() == LocalDate.now().getYear() - 1)) {
			return true;
		}
        if (date.getMonth().equals(month.minus(1)) && (date.getYear() == LocalDate.now().getYear() || date.getYear() == LocalDate.now().getYear() - 1)) {
			return true;
		}
        return date.getMonth().equals(month.minus(2)) && (date.getYear() == LocalDate.now().getYear() || date.getYear() == LocalDate.now().getYear() - 1);
    }

    public static boolean isNextMonth(YearMonth selectedMonth, LocalDate date) {
	    return YearMonth.from(date).equals(selectedMonth.plusMonths(1));
    }

    public static boolean isPrevMonth(YearMonth selectedMonth, LocalDate date) {
        return YearMonth.from(date).equals(selectedMonth.minusMonths(1));
    }
}

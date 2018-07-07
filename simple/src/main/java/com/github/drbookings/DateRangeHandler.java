package com.github.drbookings;

import java.time.LocalDate;
import java.time.YearMonth;

import com.google.common.collect.Range;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-24
 *
 */
public class DateRangeHandler {

    private final Range<LocalDate> dateRange;

    public DateRangeHandler(final Range<LocalDate> dates) {
	this.dateRange = dates;
    }

    public DateRangeHandler(final YearMonth month) {
	this.dateRange = LocalDates.toDateRange(month);
    }

    public Range<LocalDate> getDateRange() {
	return dateRange;
    }

}

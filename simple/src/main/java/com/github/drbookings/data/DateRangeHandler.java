package com.github.drbookings.data;

import java.time.LocalDate;
import java.time.YearMonth;

import com.google.common.collect.Range;

/**
 *
 * @author Alexander Kerner
 * @date 2018-06-24
 * @deprecated
 *
 */
@Deprecated
public class DateRangeHandler {

    private final Range<LocalDate> dates;

    public DateRangeHandler(final Range<LocalDate> dates) {
	this.dates = dates;
    }

    public DateRangeHandler(final YearMonth of) {
	this.dates = Range.closed(of.atDay(01), of.atEndOfMonth());
    }

    public Range<LocalDate> getDates() {
	return dates;
    }

}

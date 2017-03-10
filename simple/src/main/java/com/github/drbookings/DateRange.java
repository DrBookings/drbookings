package com.github.drbookings;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DateRange implements Iterable<LocalDate> {

    private final LocalDate startDate;

    private final LocalDate endDate;

    public DateRange(final LocalDate startDate, final LocalDate endDate) {
	if (endDate.isBefore(startDate)) {
	    throw new IllegalArgumentException(endDate + " is before " + startDate);
	}
	this.startDate = startDate;
	this.endDate = endDate;
    }

    @Override
    public Iterator<LocalDate> iterator() {
	return stream().iterator();
    }

    public Stream<LocalDate> stream() {
	return Stream.iterate(startDate, d -> d.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1);
    }

    public List<LocalDate> toList() {
	return stream().collect(Collectors.toList());
    }
}

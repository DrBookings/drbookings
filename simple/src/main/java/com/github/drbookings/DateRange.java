package com.github.drbookings;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

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

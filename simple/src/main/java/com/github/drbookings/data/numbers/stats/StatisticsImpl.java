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

package com.github.drbookings.data.numbers.stats;

import com.github.drbookings.DataCoreDateRangeHandler;
import com.github.drbookings.DataStoreCore;
import com.google.common.collect.Range;

import java.time.LocalDate;
import java.time.YearMonth;

public class StatisticsImpl extends DataCoreDateRangeHandler implements Statistics {

    public StatisticsImpl(final Range<LocalDate> dates, final DataStoreCore data) {
	super(dates, data);

    }

    public StatisticsImpl(final YearMonth month, final DataStoreCore data) {
	super(month, data);

    }

}
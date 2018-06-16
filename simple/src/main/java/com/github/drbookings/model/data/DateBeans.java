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

package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.github.drbookings.LocalDates;
import com.github.drbookings.ui.beans.DateBean;
import com.google.common.collect.Range;

public class DateBeans {

    public static Range<LocalDate> getDateRange(final Collection<? extends DateBean> dateBeans) {
	return LocalDates
		.getDateRange(dateBeans.stream().map(b -> b.getDate()).collect(Collectors.toCollection(TreeSet::new)));
    }

}

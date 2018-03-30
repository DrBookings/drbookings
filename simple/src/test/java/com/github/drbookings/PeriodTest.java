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

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

import org.junit.Test;

public class PeriodTest {

    @Test
    public void testOneMonthPeriodDays01() {
	final Period p = Period.between(LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
	assertEquals(0, p.getDays());
    }

    @Test
    public void testOneMonthPeriodDays02() {
	final long daysElapsed = ChronoUnit.DAYS.between(LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
	assertEquals(30, daysElapsed);
    }

}

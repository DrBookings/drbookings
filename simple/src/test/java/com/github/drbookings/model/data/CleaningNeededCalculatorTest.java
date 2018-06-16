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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;

public class CleaningNeededCalculatorTest {

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    private DrBookingsDataImpl data;

    private CleaningNeededCalculator cc;

    @Before
    public void setUp() throws Exception {
	data = new DrBookingsDataImpl();
    }

    @After
    public void tearDown() throws Exception {
	data = null;
    }

    @Test
    public void testCheckInNeedsCleaning01() throws Exception {
	final LocalDate checkIn = LocalDate.of(2014, 04, 21);
	final LocalDate checkOut = LocalDate.of(2014, 04, 24);
	data.addBooking(TestUtils.getTestBooking(checkIn, checkOut));
	final boolean cleaningNeeded = CleaningNeededCalculator.cleaningNeeded(checkIn, TestUtils.TEST_ROOM_NAME, data,
		null);
	assertThat(cleaningNeeded, is(false));

    }

    @Test
    public void testCheckOutNeedsCleaning01() throws Exception {
	final LocalDate checkIn = LocalDate.of(2014, 04, 21);
	final LocalDate checkOut = LocalDate.of(2014, 04, 24);
	data.addBooking(TestUtils.getTestBooking(checkIn, checkOut));
	final boolean cleaningNeeded = CleaningNeededCalculator.cleaningNeeded(checkOut, TestUtils.TEST_ROOM_NAME, data,
		null);
	assertThat(cleaningNeeded, is(true));

    }

    @Test
    public void testEmptyData01() throws Exception {
	final boolean cleaningNeeded = CleaningNeededCalculator.cleaningNeeded(LocalDate.now(),
		TestUtils.TEST_ROOM_NAME, data, null);
	assertThat(cleaningNeeded, is(false));

    }

    @Test
    public void testStayOverCleaning01() throws Exception {
	final LocalDate checkIn = LocalDate.of(2014, 04, 21);
	final LocalDate checkOut = LocalDate.of(2014, 04, 24);
	data.addBooking(TestUtils.getTestBooking(checkIn, checkOut));
	final boolean cleaningNeeded = CleaningNeededCalculator.cleaningNeeded(checkIn.plusDays(1),
		TestUtils.TEST_ROOM_NAME, data, null);
	assertThat(cleaningNeeded, is(false));

    }
}
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

import com.github.drbookings.BookingBean;
import com.github.drbookings.Bookings;
import com.github.drbookings.TestUtils;
import org.junit.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BookingsTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testNumberOfNights01() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 20));
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 05)), is(5L));
    }

    @Test
    public void testNumberOfNights02() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 20));
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 06)), is(0L));
    }

    @Test
    public void testNumberOfNights03() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 30), LocalDate.of(2015, 06, 02));
	assertThat(Bookings.countNights(Arrays.asList(bb)), is(3L));
    }

    @Test
    public void testNumberOfNights04() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 30), LocalDate.of(2015, 06, 02));
	// nights are 'shifted' by 1 towards check-out. The night counts to the day
	// before, not the day after.
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 05)), is(2L));
    }

    @Test
    public void testNumberOfNights05() {
	// one night, check-in on last day of month, check-out on first day of next
	// month
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 01, 31), LocalDate.of(2015, 02, 01));
	// nights are 'shifted' by 1 towards check-out. The night counts to the day
	// before, not the day after.
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 01)), is(1L));
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 02)), is(0L));
    }

}

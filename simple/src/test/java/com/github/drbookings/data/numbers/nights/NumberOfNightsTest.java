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

package com.github.drbookings.data.numbers.nights;

import com.github.drbookings.*;
import org.junit.Test;
import org.junit.*;
import org.junit.experimental.categories.Category;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@Category(IntegrationTests.class)
public class NumberOfNightsTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	b = new BookingBean(new Guest("Clients"), new Room("4"), new BookingOrigin(""), LocalDate.of(2018, 5, 29),
		LocalDate.of(2018, 6, 2));
    }

    @After
    public void tearDown() throws Exception {
	b = null;

    }

    private BookingBean b;

    @Test
    public void test01() {
	final long nights = LocalDates.getNumberOfNights(b.getCheckIn(), b.getCheckOut());
	assertThat(nights, is(4L));
    }

    @Test
    public void test02() {
	final long nights = Bookings.countNights(Arrays.asList(b));
	assertThat(nights, is(4L));
    }

    @Test
    public void test03() {

	final long nights = new NumberOfNightsCounter(Arrays.asList(b)).setIgnorePaymentDate(true).countNights();
	assertThat(nights, is(4L));
    }

    @Test
    public void test04() {

	final long nights5 = new NumberOfNightsCounter(YearMonth.of(2018, 5), Arrays.asList(b))
		.setIgnorePaymentDate(true).countNights();

	final long nights6 = new NumberOfNightsCounter(YearMonth.of(2018, 6), Arrays.asList(b))
		.setIgnorePaymentDate(true).countNights();

	assertThat(nights5, is(3L));
	assertThat(nights6, is(1L));

	assertThat(nights5 + nights6, is(4L));
    }

}

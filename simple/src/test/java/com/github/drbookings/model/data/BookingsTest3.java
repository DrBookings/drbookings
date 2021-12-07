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
import com.google.common.collect.Range;
import org.junit.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BookingsTest3 {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 10, 15));
    }

    @After
    public void tearDown() throws Exception {
	bb = null;
    }

    private BookingBean bb;

    @Test
    public void testNumberOfNights01() {

	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 05)), is(31L - 15 + 1));
    }

    @Test
    public void testNumberOfNights02() {

	// nights are 'shifted' towards check-out. 5 is 'stealing' 1 from 6, 6 is
	// 'stealing' 1 from 7. Therefore, if check-in and check-out are both outside of
	// the given range( in this case 6), number of nights equals number of days.
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 06)), is(30L));
    }

    @Test
    public void testNumberOfNights03() {

	assertThat(Bookings.countNights(Arrays.asList(bb),
		Range.closed(LocalDate.of(2015, 6, 1), LocalDate.of(2015, 8, 1))), is(30L + 31 + 1));
    }

}

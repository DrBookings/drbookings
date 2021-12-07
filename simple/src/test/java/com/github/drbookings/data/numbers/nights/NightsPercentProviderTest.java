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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingOrigin;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.Numbers;
import com.github.drbookings.TestUtils;

public class NightsPercentProviderTest {

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
	p = null;
    }

    private NightsPercentProvider p;

    @Test
    public void test01() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(Arrays.asList(b1, b2));
	assertThat(NightsPercentProvider.countNights(new BookingOrigin("booking"), bbo.getAllBookings()), is(8L));

    }

    @Test
    public void test02() {
	final BookingBean o1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 19),
		new BookingOrigin(""));
	final BookingBean a2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 16), LocalDate.of(2015, 05, 20),
		new BookingOrigin("airbnb"));
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingBean c1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(Arrays.asList(o1, a2, b1, c1));
	p = new NightsPercentProvider(bbo);
	// without cheating, its 1/4 of bookings
	assertThat(p.getPercent("airbnb", false), is(Numbers.getDefault(1 / 4d)));
	// with cheating, it is only 1/3
	assertThat(p.getPercent("airbnb", true), is(Numbers.getDefault(1 / 3d)));

	assertThat(p.getPercent("booking", false), is(Numbers.getDefault(1 / 2d)));

	assertThat(p.getPercent("booking", true), is(Numbers.getDefault(2 / 3d)));
    }

}

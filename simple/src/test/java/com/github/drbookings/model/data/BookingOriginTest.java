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

import com.github.drbookings.BookingOrigin;
import org.junit.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BookingOriginTest {

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
	o1 = null;
	o2 = null;
    }

    private BookingOrigin o1;
    private BookingOrigin o2;

    @Test
    public void testEqualsHashCode01() {
	o1 = new BookingOrigin("booking");
	o2 = new BookingOrigin("booking");

	assertThat(o1, is(o2));
	assertThat(o1.hashCode(), is(o2.hashCode()));
    }

}

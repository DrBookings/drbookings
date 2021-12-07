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

package com.github.drbookings.data;

import com.github.drbookings.BookingEntry;
import com.github.drbookings.Bookings;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimplePaymentFilterTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	final XMLStorage storage = new XMLStorage();
	final DataStore store = storage.load(new File("src" + File.separator + "test" + File.separator + "resources"
		+ File.separator + "test-data-2018-05-payment-filter-01.xml"));
	bbo = Bookings.toEntries(DataStore.transform(store.getBookingsSer()));
    }

    @After
    public void tearDown() throws Exception {
	bbo = null;
    }

    private List<BookingEntry> bbo;

    @Test
    public void test01() throws Exception {

	// wrong date
	final long cnt = bbo.stream().filter(new SimplePaymentFilter(YearMonth.of(2017, 05))).count();
	assertThat(cnt, is(0l));

    }

    @Test
    public void test02() throws Exception {

	final long cnt = bbo.stream().filter(new SimplePaymentFilter(YearMonth.of(2018, 05))).count();
	assertThat(cnt, is(31l));

    }

}

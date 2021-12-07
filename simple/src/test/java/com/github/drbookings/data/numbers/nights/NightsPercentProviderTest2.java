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

import java.io.File;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.Numbers;
import com.github.drbookings.PaymentDateFilter;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;

public class NightsPercentProviderTest2 {

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
		+ File.separator + "test-data-2018-05.xml"));
	bookings = DataStore.transform(store.getBookingsSer());
	p = new NightsPercentProvider(new BookingsByOrigin<>(bookings));

    }

    @After
    public void tearDown() throws Exception {
	p = null;
	bookings = null;
    }

    private NightsPercentProvider p;

    private List<BookingBean> bookings;

    @Test
    public void test01() throws Exception {
	System.err.println(bookings.stream().filter(new PaymentDateFilter(YearMonth.of(2018, 05)))
		.map(BookingBean::toTSV).collect(Collectors.joining("\n")));
	final BigDecimal bookingCnt = p.getPercent("Booking", false);
	assertThat(bookingCnt, is(Numbers.getDefault(64 / 206d)));

    }

}

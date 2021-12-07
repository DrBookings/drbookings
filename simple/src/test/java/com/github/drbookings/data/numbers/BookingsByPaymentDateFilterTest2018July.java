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

package com.github.drbookings.data.numbers;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.DataStoreCore;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BookingsByPaymentDateFilterTest2018July {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	dataSer = new FromXMLReader().readFromFile(testFile);
	data = DataStoreFactory.build(dataSer);
    }

    @After
    public void tearDown() throws Exception {
	data = null;
	dataSer = null;
    }

    private static final String fileName = "test-data-2018-07";

    private static final YearMonth month = YearMonth.of(2018, 7);

    private static final File testFile = new File(
	    "src" + File.separator + "test" + File.separator + "resources" + File.separator + fileName + ".xml");

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void test01() {

	BookingsByOrigin<BookingBean> bo = new BookingsByOrigin<>(data.getBookings());

	final long airbnb = new BookingsByPaymentDateFilter(month, bo.getAirbnbBookings()).count();

	final long bookings = new BookingsByPaymentDateFilter(month, bo.getBookingBookings()).count();

	assertThat(airbnb, is(5L));

	assertThat(bookings, is(17L));




    }

}

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

package com.github.drbookings.data.payment;

import com.github.drbookings.*;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;
import org.junit.Test;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NumberOfNightsCounterTest2018Sept {

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

    private static final File testFile = new File("src" + File.separator + "test" + File.separator + "resources"
	    + File.separator + "test-data-2018-09_2.xml");

    private static final YearMonth month = YearMonth.of(2018, 9);

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void testBooking01() {

	for (final BookingBean b : new BookingsByOrigin<>(data.getBookings()).getBookingBookings()) {
	    System.err.println(BookingBeans.toString(b) + " "
		    + new NumberOfNightsCounter(month, b).setIgnorePaymentDate(true).countNights());
	}

	final long nights = new NumberOfNightsCounter(month,
		new BookingsByOrigin<>(data.getBookings()).getBookingBookings()).setIgnorePaymentDate(true)
			.countNights();
	assertThat(nights, is(98L));
    }

    @Test
    public void testAirbnb01() {
	final long nights = new NumberOfNightsCounter(month,
		new BookingsByOrigin<>(data.getBookings()).getAirbnbBookings()).setIgnorePaymentDate(true)
			.countNights();
	assertThat(nights, is(3L));
    }

    @Test
    public void testOther01() {
	final long nights = new NumberOfNightsCounter(month,
		new BookingsByOrigin<>(data.getBookings()).getOtherBookings()).setIgnorePaymentDate(true).countNights();
	assertThat(nights, is(5L));
    }

    @Test
    public void testAll01() {
	final long nights = new NumberOfNightsCounter(month,
		new BookingsByOrigin<>(data.getBookings()).getAllBookings()).setIgnorePaymentDate(true).countNights();
	assertThat(nights, is(98L + 3 + 5));
    }

}

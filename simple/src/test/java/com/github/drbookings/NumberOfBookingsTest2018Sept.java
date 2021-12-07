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

package com.github.drbookings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.time.YearMonth;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;

@Category({ IntegrationTest.class })
public class NumberOfBookingsTest2018Sept {

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

    private static final String fileName = "test-data-2018-09_2";

    private final YearMonth month = YearMonth.of(2018, 9);

    private final static File testFile = new File(
	    "src" + File.separator + "test" + File.separator + "resources" + File.separator + fileName + ".xml");

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void testBooking01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(data.getBookings());

	// 28 im remittance advice, 1 split-zahlung, ein refund = 26
	// eine fehlende buchung = 27
	// 3 buchungen out of payment scope = 30
	assertThat(bbo.getBookingBookings().size(), is(30));
    }

    @Test
    public void testAirbnb01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(data.getBookings());
	assertThat(bbo.getAirbnbBookings().size(), is(1));

    }

    @Test
    public void testOther01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(data.getBookings());

	assertThat(bbo.getOtherBookings().size(), is(1));

    }

}

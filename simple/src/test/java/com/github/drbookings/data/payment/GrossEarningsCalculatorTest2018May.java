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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.time.YearMonth;

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.DataStoreCore;
import com.github.drbookings.DefaultGrossPaymentsSupplier;
import com.github.drbookings.Payments;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;

public class GrossEarningsCalculatorTest2018May {

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
	    + File.separator + "test-data-2018-05_2.xml");

    private static final YearMonth month = YearMonth.of(2018, 5);

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void testBooking01() {
	final MonetaryAmount a = new DefaultGrossPaymentsSupplier(month)
		.apply(new BookingsByOrigin<>(data.getBookings()).getBookingBookings());
	assertThat(a.with(Monetary.getDefaultRounding()), is(Payments.createMondary(4038.95)));
    }

}

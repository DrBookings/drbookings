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

import com.github.drbookings.DataStoreCore;
import com.github.drbookings.DefaultFixedCostsAnteilCalculator;
import com.github.drbookings.Payments;
import com.github.drbookings.data.History;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;

public class FixkostenAnteilCalculatorTest2018July {

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

    private static final File testFile = new File("src" + File.separator + "test" + File.separator + "resources"
	    + File.separator + fileName + ".xml");

    private static final YearMonth month = YearMonth.of(2018, 7);

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void testBooking01() {
	final MonetaryAmount anteil = new DefaultFixedCostsAnteilCalculator(month, data.getBookings())
		.getFixkostenAnteil("Booking", true, Payments.getSum(History.getCommonExpenses2018July()));
	assertThat(anteil.with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(1670.209279).with(Monetary.getDefaultRounding())));
    }

    @Test
    public void testAirbnb01() {
	final MonetaryAmount anteil = new DefaultFixedCostsAnteilCalculator(month, data.getBookings())
		.getFixkostenAnteil("Airbnb", true, Payments.getSum(History.getCommonExpenses2018July()));
	assertThat(anteil.with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(412.8607207).with(Monetary.getDefaultRounding())));
    }

}

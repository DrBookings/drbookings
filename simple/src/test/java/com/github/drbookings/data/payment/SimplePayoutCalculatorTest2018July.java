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

import com.github.drbookings.DataStoreCore;
import com.github.drbookings.Payments;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;
import org.junit.*;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.File;
import java.time.YearMonth;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SimplePayoutCalculatorTest2018July {

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

    private final static String fileName = "test-data-2018-07";

    private static final File testFile = new File("src" + File.separator + "test" + File.separator + "resources"
	    + File.separator + fileName + ".xml");

    private static final double pf = 0.7;

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void testBooking01() {
	final MonetaryAmount a = new DefaultPayoutSupplier(YearMonth.of(2018, 07), data.getBookings(),
		data.getCleanings(), data.getExpenses()).getPayout("Booking", true);
	assertThat(a.with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(2998.500721).with(Monetary.getDefaultRounding())));
    }

    @Test
    public void testBooking02() {
	final MonetaryAmount a = new DefaultPayoutSupplier(YearMonth.of(2018, 07), data.getBookings(),
		data.getCleanings(), data.getExpenses()).setPayoutFactor(pf).getPayout("Booking", true);
	assertThat(a.with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(2998.500721 * pf).with(Monetary.getDefaultRounding())));
    }

    @Test
    public void testAirbnb01() {

	System.err.println(data.getCleanings().stream().map(Object::toString).collect(Collectors.joining("\n")));

	final MonetaryAmount a = new DefaultPayoutSupplier(YearMonth.of(2018, 07), data.getBookings(),
		data.getCleanings(), data.getExpenses()).getPayout("Airbnb", true);
	assertThat(a.with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(632.2992793)
			.with(Monetary.getDefaultRounding())));
    }

    @Test
    public void testAirbnb02() {
	final MonetaryAmount a = new DefaultPayoutSupplier(YearMonth.of(2018, 07), data.getBookings(),
		data.getCleanings(), data.getExpenses()).setPayoutFactor(pf).getPayout("Airbnb", true);
	assertThat(a.with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(632.2992793 * pf)
			.with(Monetary.getDefaultRounding())));
    }

}

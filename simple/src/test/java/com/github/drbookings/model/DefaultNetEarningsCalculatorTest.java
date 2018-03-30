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

package com.github.drbookings.model;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.ui.CleaningEntry;
import org.junit.*;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;

public class DefaultNetEarningsCalculatorTest {

	@BeforeClass
    public static void setUpBeforeClass() {
	}

	@AfterClass
    public static void tearDownAfterClass() {
	}

	@Before
    public void setUp() {
	}

	@After
    public void tearDown() {
	}

	@Test
	public void test() {
		final DefaultNetEarningsCalculator c = new DefaultNetEarningsCalculator();
        final BookingBean b = TestUtils.getTestBooking(LocalDate.now(), LocalDate.now().plusDays(4));
		b.setGrossEarningsExpression("360");
		b.setServiceFee(0);
		b.setServiceFeesPercent(12f);
		b.setCleaningFees(60);
		final CleaningEntry ce = new CleaningEntry(LocalDate.now(), b, new Cleaning("testCleaning"), null);
		ce.setCleaningCosts(40);
		b.setCleaning(ce);
		assertEquals(360 - ((360 - 60) * 0.12), c.apply(b).doubleValue(), 0.001);

	}

}

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

import com.github.drbookings.BookingBean;
import com.github.drbookings.NumberOfNightsPercentageCalculator;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertThat;

public class NumberOfNightsPercentageCalculatorTest2018May {

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

    }

    @After
    public void tearDown() throws Exception {
	bookings = null;
    }

    private List<BookingBean> bookings;

    @Test
    public void testBookingCheckOut01() {
	final double percent = new NumberOfNightsPercentageCalculator(YearMonth.of(2018, 05), bookings)
		.getPercentageForOrigin("Booking", false);
	assertThat(percent, closeTo(0.5688, 0.0001));
    }

    @Test
    public void testBookingStay01() {
	final double percent = new NumberOfNightsPercentageCalculator(YearMonth.of(2018, 05), bookings)
		.setIgnorePaymentDate(true).getPercentageForOrigin("Booking", false);
	assertThat(percent, closeTo(0.5688, 0.0001));
    }

    @Test
    public void testBooking02() {
	final double percent = new NumberOfNightsPercentageCalculator(YearMonth.of(2018, 05), bookings)
		.getPercentageForOrigin("Booking", true);
	assertThat(percent, closeTo(0.5849, 0.0001));
    }

    @Test
    public void testAirbnb01() {
	final double percent = new NumberOfNightsPercentageCalculator(YearMonth.of(2018, 05), bookings)
		.getPercentageForOrigin("Airbnb", false);
	assertThat(percent, closeTo(0.4037, 0.0001));
    }

    @Test
    public void testAirbnb02() {
	final double percent = new NumberOfNightsPercentageCalculator(YearMonth.of(2018, 05), bookings)
		.getPercentageForOrigin("Airbnb", true);
	assertThat(percent, closeTo(0.4151, 0.0001));
    }

}

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
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.NumberOfNightsCounter;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class NumberOfNightsCounterTest2018June {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	final XMLStorage storage = new XMLStorage();
	final DataStore store = storage.load(testFile);
	bookings = DataStore.transform(store.getBookingsSer());

    }

    @After
    public void tearDown() throws Exception {
	bookings = null;
    }

    private static final File testFile = new File(
	    "src" + File.separator + "test" + File.separator + "resources" + File.separator + "test-data-2018-06.xml");

    private static final YearMonth month = YearMonth.of(2018, 6);

    private List<BookingBean> bookings;

    @Test
    public void testBookingCheckOut01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = new NumberOfNightsCounter(month, bbo.getBookingBookings())
		.setIgnorePaymentDate(false).countNights();
	assertThat(numberOfNights, is(49L));
    }

    @Test
    public void testBookingStay01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = new NumberOfNightsCounter(month, bbo.getBookingBookings())
		.setIgnorePaymentDate(true).countNights();
	assertThat(numberOfNights, is(59L));
    }

    @Test
    public void testAirbnb01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = new NumberOfNightsCounter(month, bbo.getAirbnbBookings()).setIgnorePaymentDate(true)
		.countNights();
	assertThat(numberOfNights, is(25L));
    }

    @Test
    public void testOther01CheckOut() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = new NumberOfNightsCounter(month, bbo.getOtherBookings()).setIgnorePaymentDate(false)
		.countNights();
	assertThat(numberOfNights, is(20L));
    }

    @Test
    public void testOther01Stay() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = new NumberOfNightsCounter(month, bbo.getOtherBookings()).setIgnorePaymentDate(true)
		.countNights();
	assertThat(numberOfNights, is(20L + 1));
    }

}

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

package com.github.drbookings.model.data;

import com.github.drbookings.BookingBean;
import com.github.drbookings.Bookings;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.CleaningEntry;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BookingsTest2 {

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
	// cleanings = DataStore.transformCleanings(store.getCleaningsSer(), bookings);
    }

    @After
    public void tearDown() throws Exception {
    }

    private List<BookingBean> bookings;
    private List<CleaningEntry> cleanings;

    @Test
    public void testNumberOfNights01() {

	assertThat(Bookings.countNights(new BookingsByOrigin<>(bookings).getAirbnbBookings(), YearMonth.of(2018, 05)),
		is(4 + 5 + 5 + 30L));
    }

    @Test
    public void testNumberOfNights02() {

    }

    @Test
    public void testNumberOfNights03() {

    }

    @Test
    public void testNumberOfNights04() {

    }

}

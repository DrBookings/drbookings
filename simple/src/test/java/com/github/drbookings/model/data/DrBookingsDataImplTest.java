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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.ui.CleaningEntry;

public class DrBookingsDataImplTest {

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    private DrBookingsDataImpl data;

    @Before
    public void setUp() throws Exception {
	data = new DrBookingsDataImpl();
    }

    @After
    public void tearDown() throws Exception {
	data = null;
    }

    @Test
    public void testAddBooking01() throws Exception {
	data.addBooking(new BookingBean(TestUtils.getTestGuest(), TestUtils.getTestRoom(),
		TestUtils.getTestBookingOrigin(), LocalDate.of(2018, 05, 01), LocalDate.of(2018, 05, 04)));
	assertThat(data.isEmtpy(), is(false));
    }

    @Test
    public void testCreateAndAddBooking01() throws Exception {

	final BookingBean b = data.createAndAddBooking(null, LocalDate.now(), LocalDate.now().plusDays(2), "testg", "3",
		"booking");
	assertThat(b, is(not(nullValue())));
	final List<BookingBean> bookings = data.getBookings();
	assertThat(bookings, is(not(nullValue())));
	assertThat(bookings.size(), is(1));
	assertThat(bookings.get(0), sameInstance(b));
    }

    @Test
    public void testCreateAndAddBooking02() throws Exception {

	final BookingBean b = data.createAndAddBooking(null, LocalDate.now(), LocalDate.now().plusDays(2), "testg", "3",
		"booking");
	final BookingBean b2 = data.createAndAddBooking(null, LocalDate.now().plusDays(2), LocalDate.now().plusDays(5),
		"testg", "3", "booking");
	final List<BookingBean> bookings = data.getBookings();
	assertThat(bookings, is(not(nullValue())));
	assertThat(bookings.size(), is(2));

    }

    @Test
    public void testCreateAndAddBooking03() throws Exception {
	data.createAndAddBooking(LocalDate.of(2018, 05, 01), LocalDate.of(2018, 05, 04), "g", "r", "s");
	assertThat(data.isEmtpy(), is(false));

    }

    @Test
    public void testCreateCleaningEntry01() throws Exception {

	final CleaningEntry ce = data.addCleaning("testc", LocalDate.now(), "3");
	assertThat(ce, is(not(nullValue())));
	final Cleaning c = ce.getElement();
	assertThat(c, is(not(nullValue())));
	assertThat(c.getName(), is("testc"));

    }

    @Test
    public void testCreateCleaningEntry02() throws Exception {

	final CleaningEntry ce = data.addCleaning("testc", LocalDate.now(), "3");
	final List<CleaningEntry> entries = data.getCleaningEntries();
	assertThat(entries, is(not(nullValue())));
	assertThat(entries.size(), is(1));
	assertThat(entries.get(0), sameInstance(ce));

    }

    @Test(expected = AlreadyBusyException.class)
    public void testCreateCleaningEntry03() throws Exception {

	final CleaningEntry ce = data.addCleaning("testc", LocalDate.now(), "3");
	final CleaningEntry ce2 = data.addCleaning("testc", LocalDate.now(), "3");

    }

    @Test
    public void testGetAfter01() throws Exception {
	final BookingBean b = data.createAndAddBooking(null, LocalDate.now(), LocalDate.now().plusDays(2), "testg", "3",
		"booking");
    }

    @Test
    public void testGetBookingEntry01() throws Exception {
	data.addBooking(new BookingBean(TestUtils.getTestGuest(), TestUtils.getTestRoom(),
		TestUtils.getTestBookingOrigin(), LocalDate.of(2018, 05, 01), LocalDate.of(2018, 05, 04)));
	assertThat(data.isEmtpy(), is(false));
    }

    @Test
    public void testGetBookingEntryPair01() throws Exception {
	data.createAndAddBooking(LocalDate.of(2018, 05, 01), LocalDate.of(2018, 05, 04), "g", "r", "s");
	final Optional<BookingEntryPair> bookings = data.getBookingEntryPair("r", LocalDate.of(2018, 05, 01));
	assertThat(bookings.isPresent(), is(true));

    }
}
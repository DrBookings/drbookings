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

import com.github.drbookings.model.exception.AlreadyBusyException;
import com.github.drbookings.ui.CleaningEntry;
import java.time.LocalDate;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DrBookingsDataTest {

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    @Before
    public void setUp() throws Exception {
        data = new DrBookingsData();
    }

    @After
    public void tearDown() throws Exception {
        data = null;
    }

    private DrBookingsData data;

    @Test
    public void testCreateCleaningEntry01() throws Exception {

        CleaningEntry ce = data.createCleaningEntry("testc", LocalDate.now(), "3");
        assertThat(ce, is(not(nullValue())));
        Cleaning c = ce.getElement();
        assertThat(c, is(not(nullValue())));
        assertThat(c.getName(), is("testc"));


    }

    @Test
    public void testCreateCleaningEntry02() throws Exception {

        CleaningEntry ce = data.createCleaningEntry("testc", LocalDate.now(), "3");
       List<CleaningEntry> entries = data.getCleaningEntries();
       assertThat(entries, is(not(nullValue())));
       assertThat(entries.size(), is(1));
       assertThat(entries.get(0), sameInstance(ce));


    }

    @Test(expected = AlreadyBusyException.class)
    public void testCreateCleaningEntry03() throws Exception {

        CleaningEntry ce = data.createCleaningEntry("testc", LocalDate.now(), "3");
        CleaningEntry ce2 = data.createCleaningEntry("testc", LocalDate.now(), "3");

    }

    @Test
    public void testCreateAndAddBooking01() throws Exception {

        BookingBean b = data.createAndAddBooking(null,LocalDate.now(), LocalDate.now().plusDays(2),"testg", "3", "booking");
        assertThat(b, is(not(nullValue())));
        List<BookingBean> bookings = data.getBookings();
        assertThat(bookings, is(not(nullValue())));
        assertThat(bookings.size(), is(1));
        assertThat(bookings.get(0), sameInstance(b));
    }
}
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.drbookings.model.data.DrBookingsData;
import java.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class UnifiedDataAccessTest {

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    @Before
    public void setUp() throws Exception {
        data = new DrBookingsData();
        da = new UnifiedDataAccess(LocalDate.now(), data);
    }

    @After
    public void tearDown() throws Exception {
        da = null;
        data = null;
    }

    private DrBookingsData data;

    private UnifiedDataAccess da;


    @Test
    public void test01() throws Exception {
        da.init();
        assertThat(da.getCleaningEntries(), is(not(nullValue())));
        assertThat(da.getRoomEntries(), is(not(nullValue())));
        assertThat(da.getBookingEntries(), is(not(nullValue())));

    }
}
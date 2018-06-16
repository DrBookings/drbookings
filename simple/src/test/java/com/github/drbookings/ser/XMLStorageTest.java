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

package com.github.drbookings.ser;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.DrBookingsDataImpl;

public class XMLStorageTest {

    public static final String DATA_FILE = File.separator + "test" + File.separator + "resources" + File.separator
	    + XMLStorage.class.getSimpleName();

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    private DrBookingsDataImpl data;

    private XMLStorage storage;

    @Before
    public void setUp() throws Exception {
	data = new DrBookingsDataImpl();
	storage = new XMLStorage();
    }

    @After
    public void tearDown() throws Exception {
	storage = null;
	data = null;

    }

    @Test
    public void testLoad01() throws Exception {
	final DataStore store = storage
		.load(new File("test" + File.separator + "resources" + File.separator + "bookings.xml"));
	final List<BookingBean> bookings = DataStore.transform(store.getBookingsSer());
	for (final BookingBean bb : bookings) {
	    data.addBooking(bb);
	}
    }
}
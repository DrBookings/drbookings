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

package com.github.drbookings;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import com.github.drbookings.model.data.DrBookingsDataImpl;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.controller.MainController;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class IntegrationTest01 {

    @Before
    public void setUp() throws Exception {
        mainController = new MainController();
    }

    @After
    public void tearDown() throws Exception {
        mainController = null;
    }

    private MainController mainController;


    @Test
    public void test01() throws Exception {
        DataStore data = new XMLStorage()
            .load(new File("test/resources/test-bookings/2-bookings-1-cleaing/booking-export.xml"));
        data.load(mainController.getManager());
        // 21 -> 27
        assertThat(mainController.getManager().getBookingEntryPairs().size(), is(7));
        assertThat(mainController.getManager().getBookingEntries().size(), is(8));
        assertThat(mainController.getManager().getCleaningEntries().size(), is(1));


    }

}

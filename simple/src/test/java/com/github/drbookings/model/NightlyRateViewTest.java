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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;

public class NightlyRateViewTest {

    private NightlyRateView view;

    @Before
    public void setUp() {
	view = new NightlyRateView();
    }

    @After
    public void tearDown() {
	view = null;
    }

    @Test
    public void test01() {
	final BookingBean booking1 = new BookingBean("id1", new Guest("g1"), new Room("1"), new BookingOrigin("airbnb"),
		LocalDate.of(2000, 4, 4), LocalDate.of(2000, 4, 6));
	final BookingBean booking2 = new BookingBean("id2", new Guest("g2"), new Room("2"), new BookingOrigin("airbnb"),
		LocalDate.of(2000, 4, 4), LocalDate.of(2000, 4, 6));
	booking1.setGrossEarningsExpression("100");
	booking2.setGrossEarningsExpression("200");
	final List<BookingEntry> entries1 = booking1.getEntries();
	final List<BookingEntry> entries2 = booking2.getEntries();
	// System.out.println(entries.stream().map(b ->
	// b.toString()).collect(Collectors.joining("\n")));
	view.addAll(entries1);
	view.addAll(entries2);
	assertThat(view.data.size(), is(1));

    }

}
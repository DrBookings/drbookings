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

package com.github.drbookings.data.cleaning;

import com.github.drbookings.*;
import org.junit.Test;
import org.junit.*;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CleaningNeededEvaluatorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {

	cleaningData = new SimpleCleaningData();
	bookingData = new SimpleBookingData();
	eval = new CleaningNeededEvaluator(cleaningData, bookingData);

    }

    @After
    public void tearDown() throws Exception {
	eval = null;
	cleaningData = null;
	room = null;
    }

    private CleaningNeededEvaluator eval;

    private SimpleCleaningData cleaningData;

    private SimpleBookingData bookingData;

    private RoomBean room;

    @Test
    public void testEmpty01() {
	room = new RoomBean(new Room("room"), new DateBean(LocalDate.of(2018, 05, 02)));
	assertThat(eval.evaluate(room), is(false));
    }

    @Test
    public void testNoCheckOut01() throws Exception {

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	room = new RoomBean(new Room("room"), new DateBean(LocalDate.of(2018, 05, 02)));
	room.getBookingEntry().addBooking(entries.get(0));

	assertThat(eval.evaluate(room), is(false));
    }

    @Test
    public void testCheckOut01() throws Exception {

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	room = new RoomBean(new Room("room"), new DateBean(LocalDate.of(2018, 05, 05)));
	room.getBookingEntry().addBooking(entries.get(3));

	// check out and empty cleaning data
	assertThat(eval.evaluate(room), is(true));
    }

    @Test
    public void testSomeTimeAfter01() throws Exception {

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	room = new RoomBean(new Room("room"), new DateBean(LocalDate.of(2018, 05, 05)));
	room.getBookingEntry().addBooking(entries.get(3));
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 06), null, false);
	ce.setRoom(new Room("room"));
	cleaningData.add(ce);

	// check out and empty cleaning data
	assertThat(eval.evaluate(room), is(false));
    }

}

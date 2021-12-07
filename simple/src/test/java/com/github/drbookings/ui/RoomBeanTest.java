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

package com.github.drbookings.ui;

import com.github.drbookings.*;
import org.junit.Test;
import org.junit.*;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class RoomBeanTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private DateBean date;

    private RoomBean r;

    @Before
    public void setUp() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 02));
	r = new RoomBean(new Room("r1"), date);
    }

    @After
    public void tearDown() throws Exception {
	r = null;
	date = null;

    }

    @Test
    public void testDate01() throws Exception {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDate02() throws Exception {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	// dates do not match
	r.getBookingEntry().addBooking(entries.get(1));
    }

    /*
     * Tests empty room.
     *
     */
    @Test
    public void testNeedsCleaning01() throws Exception {
	assertThat(r.needsCleaning(), is(false));
    }

    /*
     * Tests no cleaning needed for no-check-out booking entry.
     *
     */
    @Test
    public void testNeedsCleaning02() throws Exception {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(0));
	assertThat(r.needsCleaning(), is(false));
    }

    /*
     * Tests cleaning needed for check-out booking entry. Set the booking entry
     * directly.
     */
    @Test
    public void testNeedsCleaning03() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	// replace the booking entry
	r.setBookingEntry(new BookingEntryPair(entries.get(3)));
	assertThat(r.needsCleaning(), is(true)); // check-out
    }

    /*
     * Tests cleaning needed for check-out booking entry. Get and modify the booking
     * entry.
     */
    @Test
    public void testNeedsCleaning04() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	// modify the booking entry
	r.getBookingEntry().addBooking(entries.get(3));
	assertThat(r.needsCleaning(), is(true)); // check-out
    }

    /*
     * Tests for a later-on entry. First modify cleaning data, then set it.
     */
    @Test
    public void testNeedsCleaning06() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(3));

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 6), b, new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r1"));
	// one day later, query for the entry
	cleaningData.add(ce);

	r.setCleaningData(cleaningData);

	assertThat(r.needsCleaning(), is(false));
    }

    /*
     * Tests for a later-on entry. First set the cleaning data, then modify it.
     */
    @Test
    public void testNeedsCleaning07() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(3));

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 6), b, new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r1"));

	r.setCleaningData(cleaningData);

	// one day later, query for the entry
	cleaningData.add(ce);

	assertThat(r.needsCleaning(), is(false));
    }

    /*
     * Tests for a later-on entry. First modify cleaning data, then set it. Room
     * miss match.
     */
    @Test
    public void testNeedsCleaning08() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(3));

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 6), b, new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r2"));
	// one day later, query for the entry, room miss match
	cleaningData.add(ce);

	r.setCleaningData(cleaningData);

	assertThat(r.needsCleaning(), is(true));
    }

    /*
     * First modify data, then set.
     */
    @Test
    public void testCleaningEntry01() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 5), new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r1"));
	cleaningData.add(ce);
	r.setCleaningData(cleaningData);
	assertThat(r.hasCleaning(), is(true));
    }

    /*
     * First modify data, then set.
     */
    @Test
    public void testCleaningEntry02() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(3));

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 5), b, new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r1"));
	cleaningData.add(ce);
	r.setCleaningData(cleaningData);
	assertThat(r.hasCleaning(), is(true));
    }

    /*
     * First set data, then modify.
     */
    @Test
    public void testCleaningEntry03() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 5), new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r1"));
	r.setCleaningData(cleaningData);
	cleaningData.add(ce);
	assertThat(r.hasCleaning(), is(true));
    }

    /*
     * First set data, then modify.
     */
    @Test
    public void testCleaningEntry04() throws Exception {

	date = new DateBean(LocalDate.of(2018, 05, 05));
	r = new RoomBean(new Room("r1"), date);

	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2018, 05, 02), LocalDate.of(2018, 05, 05));
	final List<BookingEntry> entries = Bookings.toEntries(b);
	r.getBookingEntry().addBooking(entries.get(3));

	final UICleaningData cleaningData = new SimpleUICleaningData();
	final CleaningEntry ce = new CleaningEntry(LocalDate.of(2018, 05, 5), b, new Cleaning("cleaning"), false);
	ce.setRoom(new Room("r1"));
	r.setCleaningData(cleaningData);
	cleaningData.add(ce);
	assertThat(r.hasCleaning(), is(true));
    }

}

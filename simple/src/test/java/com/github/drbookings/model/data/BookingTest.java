package com.github.drbookings.model.data;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookingTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		b1 = null;
		b2 = null;
	}

	private Booking b1, b2;

	static Booking newInstance() {
		return new Booking(new Guest("testGuest"), new Room("testRoom"), new BookingOrigin("testOrigin"),
				LocalDate.of(2017, 06, 01), LocalDate.of(2017, 06, 04));
	}

	@Test
	public void testNumberOfDaysAndNights01() {
		final Booking b = new Booking(new Guest("testGuest"), new Room("testRoom"), new BookingOrigin("testOrigin"),
				LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
		assertEquals(31, b.getNumberOfDays());
		assertEquals(30, b.getNumberOfNights());

	}

	@Test
	public void testDateOfPayment01() {
		final Booking b = newInstance();
		assertThat(b.isPaymentDone(), is(false));
		b.setPaymentDone(true);
		assertThat(b.isPaymentDone(), is(true));
	}

	@Test
	public void testDateOfPayment02() {
		final Booking b = newInstance();
		assertThat(b.isPaymentDone(), is(false));
		assertThat(b.getDateOfPayment(), is(nullValue()));
		b.setPaymentDone(true);
		assertThat(b.getDateOfPayment(), is(LocalDate.now()));
	}

	@Test
	public void testDateOfPayment03() {
		final Booking b = newInstance();
		assertThat(b.getDateOfPayment(), is(nullValue()));
		b.setDateOfPayment(LocalDate.of(2017, 12, 12));
		assertThat(b.getDateOfPayment(), is(LocalDate.of(2017, 12, 12)));
		assertThat(b.isPaymentDone(), is(true));
	}

}

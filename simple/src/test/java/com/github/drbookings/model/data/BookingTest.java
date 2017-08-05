package com.github.drbookings.model.data;

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

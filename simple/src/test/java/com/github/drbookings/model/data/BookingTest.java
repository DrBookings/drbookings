package com.github.drbookings.model.data;

import static org.junit.Assert.assertEquals;

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
    }

    @Test
    public void testNumberOfDaysAndNights01() {
	final Booking b = new Booking(new Guest("testGuest"), new Room("testRoom"), new BookingOrigin("testOrigin"),
		LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
	assertEquals(31, b.getNumberOfDays());
	assertEquals(30, b.getNumberOfNights());

    }

}

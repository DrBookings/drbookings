package com.github.drbookings.model.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookingBeanTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private BookingBean bb;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
	bb = null;
    }

    @Test
    public void testBookingBean() {
	bb = new BookingBean();
	assertEquals(0, bb.getBruttoEarnings(), 0);
	assertEquals(0, bb.getBruttoEarningsPerNight(), 0);
	assertNull(bb.getDate());
	assertNull(bb.getGuestName());
	assertEquals(0, bb.getNumberOfTotalNights(), 0);
	assertNull(bb.getRoom());
	// empty string, due to regex matching
	assertEquals("", bb.getSource());
	assertFalse(bb.hasGuest());
    }

    @Test
    public void testBookingBeanString() {
	bb = new BookingBean("testGuest");
	assertEquals("testGuest", bb.getGuestName());
    }

    @Test
    public void testBookingBeanStringString() {
	bb = new BookingBean("testGuest", "testRoom");
	assertEquals("testGuest", bb.getGuestName());
	assertEquals(new RoomBean("testRoom"), bb.getRoom());
	assertTrue(bb.getRoom().equals(new RoomBean("testRoom")));
    }

}

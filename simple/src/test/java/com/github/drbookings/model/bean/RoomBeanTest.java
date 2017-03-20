package com.github.drbookings.model.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.OverbookingException;

public class RoomBeanTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private RoomBean rb;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
	rb = null;
    }

    @Test
    public void testAddBooking01() throws OverbookingException {
	rb = new RoomBean("testRoom");
	rb.addBooking(new BookingBean("testGuest"));
	assertEquals(1, rb.getBookings().size());
	for (final BookingBean bb : rb.getBookings()) {
	    assertEquals(rb, bb.getRoom());
	}
    }

    @Test(expected = OverbookingException.class)
    public void testAddBooking02() throws OverbookingException {
	rb = new RoomBean("testRoom");
	rb.addBooking(new BookingBean("testGuest"));
	rb.addBooking(new BookingBean("testGuest2"));
	for (final BookingBean bb : rb.getBookings()) {
	    assertEquals(rb, bb.getRoom());
	}
    }

    @Test
    public void testRoomBean() {
	rb = new RoomBean();
	assertNotNull(rb.getBookings());
	assertEquals(0, rb.getBookings().size());
	assertFalse(rb.getCheckIn().isPresent());
	assertFalse(rb.getCheckOut().isPresent());
	assertNull(rb.getCleaning());
	assertNull(rb.getDateBean());
	assertNull(rb.getDate());
	assertNull(rb.getName());
	assertFalse(rb.getConnectedNext().isPresent());
    }

    @Test
    public void testRoomBeanString() {
	rb = new RoomBean("test");
	assertEquals("test", rb.getName());
    }

    @Test
    public void testRoomBeanStringBookingBean01() {
	rb = new RoomBean("test", new BookingBean("testGuest"));
	assertEquals("test", rb.getName());
	assertEquals(1, rb.getBookings().size());
	assertEquals(new BookingBean("testGuest"), rb.getBookings().get(0));

    }

    @Test
    public void testRoomBeanStringBookingBean02() {
	final BookingBean bb = new BookingBean("testGuest");
	rb = new RoomBean("test", bb);
	assertEquals(rb, bb.getRoom());

    }

}

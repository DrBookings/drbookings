package com.github.drbookings.model.bean;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.OverbookingException;

public class DateBeanTest {

    private DateBean db;

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
    public void testAddRoom01() throws OverbookingException {
	db = new DateBean(LocalDate.now());
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	assertEquals(db, rb.getDateBean());
    }

    @Test
    public void testAddRoom02() throws OverbookingException {
	db = new DateBean(LocalDate.now());
	final RoomBean rb = new RoomBean("testRoom");
	final BookingBean bb = new BookingBean("testGuest");
	db.addRoom(rb);
	rb.addBooking(bb);
	assertEquals(bb, db.getBookings().get(0));
    }

}

package com.github.drbookings.model.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.OverbookingException;

public class DateBeanTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private DateBean db;

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
	db = null;
    }

    @Test
    public void testAddRoom01() throws OverbookingException {
	db = new DateBean();
	db.addRoom(new RoomBean("testRoom"));
	assertTrue(db.getRooms().contains(new RoomBean("testRoom")));
    }

    @Test
    public void testAddRoomBean01() throws OverbookingException {
	db = new DateBean();
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	assertEquals(5, db.getRooms().size());
	assertEquals(5, db.getRoomCount());
	assertEquals(db.getDate(), rb.getDate());
    }

    @Test
    public void testDateBean() {
	db = new DateBean();
	assertEquals(LocalDate.now(), db.getDate());
	assertNull(db.getDataModel());
	assertEquals(0, db.getAuslastung(), 0);
	assertNotNull(db.getBookings());
	assertEquals(0, db.getBookings().size());
	assertNotNull(db.getRooms());
	// rooms 1, 2, 3, 4 automatically added
	assertEquals(4, db.getRooms().size());
    }

    @Test
    public void testDateBeanLocalDate() {
	db = new DateBean(LocalDate.of(1999, 07, 24));
	assertEquals(LocalDate.of(1999, 07, 24), db.getDate());
    }

    @Test
    public void testGetBookings01() throws OverbookingException {
	db = new DateBean();
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	final BookingBean bb = new BookingBean("testGuest");
	rb.addBooking(bb);
	assertEquals(1, db.getBookings().size());
    }

    @Test
    public void testGetBookings02() throws OverbookingException {
	db = new DateBean();
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	final BookingBean bb = new BookingBean("testGuest");
	rb.addBooking(bb);
	db.addRoom(new RoomBean("testRoom2", new BookingBean("testGuest2")));
	assertEquals(2, db.getBookings().size());
    }

    @Test
    public void testGetBookings03() throws OverbookingException {
	db = new DateBean();
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	final BookingBean bb = new BookingBean("testGuest");
	rb.addBooking(bb);
	assertEquals(rb.getDate(), db.getDate());
	assertEquals(bb.getDate(), rb.getDate());
    }

    @Test
    public void testGetBookings04() throws OverbookingException {
	db = new DateBean(LocalDate.now().plusDays(3));
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	final BookingBean bb = new BookingBean("testGuest");
	rb.addBooking(bb);
	assertEquals(LocalDate.now().plusDays(3), db.getDate());
	assertEquals(LocalDate.now().plusDays(3), rb.getDate());
	assertEquals(LocalDate.now().plusDays(3), bb.getDate());
    }

    @Test
    public void testGetBookings05() throws OverbookingException {
	db = new DateBean(LocalDate.now().plusDays(3));
	final RoomBean rb = new RoomBean("testRoom");
	db.addRoom(rb);
	final BookingBean bb = new BookingBean("testGuest");
	rb.addBooking(bb);
	assertEquals(bb, db.getBookings().get(0));
	assertEquals(LocalDate.now().plusDays(3), db.getBookings().get(0).getDate());

    }

}

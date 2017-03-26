package com.github.drbookings.model.bean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.DateRange;

public class BookingBeanTest {

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
    public void testCreate01() {
	final BookingBean bb = BookingBean.create("testGuest", "testRoom", LocalDate.now());
	assertNotNull(bb.getRoom());
	assertNotNull(bb.getGuestName());
	assertNotNull(bb.getDate());

    }

    @Test
    public void testCreate02() {
	final BookingBean bb = BookingBean.create("testGuest", "testRoom", LocalDate.now());
	assertEquals(new RoomBean("testRoom"), bb.getRoom());
	assertEquals(new DateBean(LocalDate.now()), bb.getRoom().getDateBean());

    }

    @Test
    public void testCreate03() {
	final DateRange dateRange = new DateRange(LocalDate.now(), LocalDate.now().plusDays(3));

	for (final LocalDate date : dateRange) {
	    final BookingBean bb = BookingBean.create("testGuest", "testRoom", date);
	    assertEquals(new RoomBean("testRoom"), bb.getRoom());
	    assertEquals(new DateBean(date), bb.getRoom().getDateBean());
	}

    }

    @Test
    public void testCreate04() {
	final BookingBean bb = BookingBean.create("gestName", "1", LocalDate.now()).setSource("soruce");
	assertNotNull(bb);
	assertNotNull(bb.getRoom());
	assertNotNull(bb.getRoom().getDateBean());

    }

    @Test
    public void testCreate05() {
	final BookingBean bb = BookingBean.create("gestName", "testRoom", LocalDate.now()).setSource("soruce");
	assertNotNull(bb);
	assertNotNull(bb.getRoom());
	assertNotNull(bb.getRoom().getDateBean());

    }

}

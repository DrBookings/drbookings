package com.github.drbookings.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

public class DataModelTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private DataModel dm;

    @Before
    public void setUp() throws Exception {
	dm = new DataModel();
    }

    @After
    public void tearDown() throws Exception {
	dm = null;
    }

    @Test
    public void testAddBookingBean01() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	assertEquals(1, dm.getData().size());
	assertEquals(1, dm.getBookings().size());
	assertTrue(dm.getData().get(0).getRooms().contains(new RoomBean("testRoom")));
    }

    @Test(expected = OverbookingException.class)
    public void testAddBookingBean02() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
    }

    @Test(expected = OverbookingException.class)
    public void testAddBookingBean03() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	dm.add(LocalDate.now(), new BookingBean("testGuest2", "testRoom"));
    }

    @Test
    public void testAddBookingBean04() throws OverbookingException {
	dm.add(LocalDate.now(), LocalDate.now().plusDays(1), "testGuest", "testRoom");
	assertEquals(2, dm.getBookings().size());
    }

    @Test
    public void testAddBookingBean05() throws OverbookingException {
	dm.add(LocalDate.now(), LocalDate.now().plusDays(1), "testGuest", "testRoom");
	final List<BookingBean> bookings = dm.getBookings();
	assertEquals(LocalDate.now(), bookings.get(0).getDate());
	assertEquals(LocalDate.now().plusDays(1), bookings.get(1).getDate());
    }

    @Test
    public void testAddBookingBean06() throws OverbookingException {
	dm.add(LocalDate.now(), LocalDate.now().plusDays(3), "testGuest", "testRoom");
	final List<BookingBean> bookings = dm.getBookings();
	assertEquals(LocalDate.now(), bookings.get(0).getDate());
	assertEquals(LocalDate.now().plusDays(1), bookings.get(1).getDate());
	assertEquals(LocalDate.now().plusDays(2), bookings.get(2).getDate());
	assertEquals(LocalDate.now().plusDays(3), bookings.get(3).getDate());
    }

    @Test
    public void testAddDateBean01() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	assertEquals(1, dm.getData().size());
    }

    @Test
    public void testAddDateBean02() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now()));
	assertEquals(1, dm.getData().size());
    }

    @Test
    public void testAddDateBean03() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now().plusDays(1)));
	assertEquals(2, dm.getData().size());
    }

    @Test
    public void testAddDateBean04() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	// should fill-up dates automatically
	dm.add(new DateBean(LocalDate.now().plusDays(2)));
	assertEquals(3, dm.getData().size());
    }

    @Test
    public void testAddDateBean05() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	// should fill-up dates automatically
	dm.add(new DateBean(LocalDate.now().plusDays(5)));
	assertEquals(6, dm.getData().size());
    }

    @Test
    public void testGetAfter01() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now().plusDays(1)));
	assertEquals(1, dm.getAfter(LocalDate.now()).size());
	assertEquals(new DateBean(LocalDate.now().plusDays(1)), dm.getAfter(LocalDate.now()).get(0));
    }

    @Test
    public void testGetAfter02() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now().plusDays(2)));
	assertEquals(2, dm.getAfter(LocalDate.now()).size());
	assertEquals(new DateBean(LocalDate.now().plusDays(1)), dm.getAfter(LocalDate.now()).get(0));
	assertEquals(new DateBean(LocalDate.now().plusDays(2)), dm.getAfter(LocalDate.now()).get(1));
    }

    @Test
    public void testGetAllBookings01() throws OverbookingException {
	final BookingBean bb1 = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	final LocalDate date1 = LocalDate.now();
	final LocalDate date2 = date1.plusDays(1);
	dm.add(date1, bb1);
	dm.add(date2, bb2);
	final List<BookingBean> bookings = dm.getAllBookings();
	assertEquals(date1, bookings.get(0).getDate());
	assertEquals(date2, bookings.get(1).getDate());

    }

    @Test
    public void testGetAllBookings02() throws OverbookingException {
	final BookingBean bb1 = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	final LocalDate date1 = LocalDate.now();
	final LocalDate date2 = date1.plusDays(2);
	dm.add(date1, bb1);
	dm.add(date2, bb2);
	final List<BookingBean> bookings = dm.getAllBookings();
	assertEquals(date1, bookings.get(0).getDate());
	assertEquals(date2, bookings.get(1).getDate());

    }

    @Test
    public void testGetAllBookings03() throws OverbookingException {
	final BookingBean bb1 = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	final LocalDate date1 = LocalDate.now();
	final LocalDate date2 = date1.plusDays(2);
	dm.add(date1, bb1);
	dm.add(date2, bb2);
	final List<BookingBean> bookings = dm.getAllBookings();
	assertEquals(date1, bookings.get(0).getDate());
	assertEquals(date2, bookings.get(1).getDate());
    }

    @Test
    public void testGetAllBookings04() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	final List<BookingBean> bookings = dm.getAllBookings();
	assertEquals(startDate, bookings.get(0).getDate());
	assertEquals(startDate.plusDays(1), bookings.get(1).getDate());
	assertEquals(startDate.plusDays(2), bookings.get(2).getDate());
	assertEquals(startDate.plusDays(3), bookings.get(3).getDate());
    }

    @Test
    public void testGetAllBookingsStream01() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	final List<BookingBean> bookings = dm.getAllBookingsStream().collect(Collectors.toList());
	assertEquals(1, bookings.size());
    }

    @Test
    public void testGetAllBookingsStream02() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	dm.add(LocalDate.now().plusDays(3), new BookingBean("testGuest2", "testRoom2"));
	final List<BookingBean> bookings = dm.getAllBookingsStream().collect(Collectors.toList());
	assertEquals(2, bookings.size());
    }

    @Test
    public void testGetAllBookingsStream03() throws OverbookingException {
	final BookingBean bb1 = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb1);
	dm.add(LocalDate.now().plusDays(1), bb2);
	final List<BookingBean> bookings = dm.getAllBookingsStream().collect(Collectors.toList());
	assertEquals(bb1, bookings.get(0));
	assertEquals(bb2, bookings.get(1));
    }

    @Test
    public void testGetAllBookingsStream04() throws OverbookingException {
	final BookingBean bb1 = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	// stream should be sorted
	dm.add(LocalDate.now().plusDays(1), bb2);
	dm.add(LocalDate.now(), bb1);
	final List<BookingBean> bookings = dm.getAllBookingsStream().collect(Collectors.toList());
	assertEquals(bb1, bookings.get(0));
	assertEquals(bb2, bookings.get(1));
    }

    @Test
    public void testGetAllBookingsStream05() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	final List<BookingBean> bookings = dm.getAllBookingsStream().collect(Collectors.toList());
	assertEquals(startDate, bookings.get(0).getDate());
	assertEquals(startDate.plusDays(1), bookings.get(1).getDate());
	assertEquals(startDate.plusDays(2), bookings.get(2).getDate());
	assertEquals(startDate.plusDays(3), bookings.get(3).getDate());
    }

    @Test
    public void testGetAllBookingsStreamBetween01() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	final List<BookingBean> bookings = dm.getAllBookingsStreamBetween(LocalDate.now(), LocalDate.now())
		.collect(Collectors.toList());
	assertEquals(1, bookings.size());
    }

    @Test
    public void testGetAllBookingsStreamBetween02() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	dm.add(LocalDate.now().plusDays(2), new BookingBean("testGuest2", "testRoom2"));
	final List<BookingBean> bookings = dm.getAllBookingsStreamBetween(LocalDate.now(), LocalDate.now().plusDays(2))
		.collect(Collectors.toList());
	assertEquals(2, bookings.size());
    }

    @Test
    public void testGetAllBookingsStreamBetween03() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	dm.add(LocalDate.now().plusDays(3), new BookingBean("testGuest2", "testRoom2"));
	final List<BookingBean> bookings = dm.getAllBookingsStreamBetween(LocalDate.now(), LocalDate.now().plusDays(2))
		.collect(Collectors.toList());
	assertEquals(1, bookings.size());
    }

    @Test
    public void testGetAllBookingsStreamBetween04() throws OverbookingException {
	final BookingBean bb1 = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb1);
	dm.add(LocalDate.now().plusDays(3), bb2);
	final List<BookingBean> bookings = dm.getAllBookingsStreamBetween(LocalDate.now(), LocalDate.now().plusDays(3))
		.collect(Collectors.toList());
	assertEquals(2, bookings.size());
    }

    @Test
    public void testGetByGuestName01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	final Map<String, List<BookingBean>> map = dm.getByGuestName(startDate, endDate);
	assertEquals(1, map.size());
	assertEquals(4, map.get("testGuest").size());
    }

    @Test
    public void testGetByGuestName02() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	final LocalDate startDate2 = LocalDate.now().plusDays(5);
	final LocalDate endDate2 = startDate2.plusDays(3);
	dm.add(startDate2, endDate2, bb);
	final Map<String, List<BookingBean>> map = dm.getByGuestName(startDate, LocalDate.now().plusDays(8));
	assertEquals(1, map.size());
	assertEquals(8, map.get("testGuest").size());
    }

    @Test
    public void testGetByGuestName03() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	final Map<String, List<BookingBean>> map = dm.getByGuestName(startDate, endDate);
	assertEquals(1, map.size());
	assertEquals(4, map.get("testGuest").size());
    }

    @Test
    public void testGetByGuestName04() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	final Map<String, List<BookingBean>> map = dm.getByGuestName(startDate, endDate);
	final List<BookingBean> values = map.get("testGuest");
	assertEquals(LocalDate.now(), values.get(0).getDate());
	assertEquals(LocalDate.now().plusDays(1), values.get(1).getDate());
	assertEquals(LocalDate.now().plusDays(2), values.get(2).getDate());
	assertEquals(LocalDate.now().plusDays(3), values.get(3).getDate());
    }

    @Test
    public void testGetConnectedNext01() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	assertFalse(dm.getConnectedNext(new DateBean(LocalDate.now())).isPresent());

    }

    @Test
    public void testGetConnectedNext02() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now().plusDays(1)));
	assertTrue(dm.getConnectedNext(new DateBean(LocalDate.now())).isPresent());

    }

    @Test
    public void testGetConnectedNext03() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now().plusDays(2)));
	// true, since filled up
	assertTrue(dm.getConnectedNext(new DateBean(LocalDate.now())).isPresent());

    }

    @Test
    public void testGetConnectedNextBookingBean01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	dm.add(LocalDate.now().plusDays(1), bb2);
	assertTrue(dm.getConnectedNext(bb).isPresent());
	assertEquals(bb2, dm.getConnectedNext(bb).get());

    }

    @Test
    public void testGetConnectedNextDateBean01() throws OverbookingException {
	dm.add(new DateBean(LocalDate.now()));
	dm.add(new DateBean(LocalDate.now().plusDays(1)));
	assertTrue(dm.getConnectedNext(new DateBean(LocalDate.now())).isPresent());
	assertEquals(new DateBean(LocalDate.now().plusDays(1)),
		dm.getConnectedNext(new DateBean(LocalDate.now())).get());

    }

    @Test
    public void testGetConnectedPreviousBookingBean01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	dm.add(LocalDate.now().plusDays(1), bb2);
	assertTrue(dm.getConnectedPrevious(bb2).isPresent());
	assertEquals(bb, dm.getConnectedPrevious(bb2).get());

    }

    @Test
    public void testGetNumberOfBookingDays01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	assertEquals(1, dm.getNumberOfBookingDays(LocalDate.now(), LocalDate.now()));
    }

    @Test
    public void testGetNumberOfBookingDays02() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), LocalDate.now().plusDays(3), bb);
	assertEquals(4, dm.getNumberOfBookingDays(LocalDate.now(), LocalDate.now().plusDays(3)));
    }

    @Test
    public void testGetNumberOfBookingNights01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	assertEquals(0, dm.getNumberOfBookingNights(LocalDate.now(), LocalDate.now()));
    }

    @Test
    public void testGetNumberOfBookingNights02() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), LocalDate.now().plusDays(3), bb);
	assertEquals(3, dm.getNumberOfBookingNights(LocalDate.now(), LocalDate.now().plusDays(3)));
    }

    @Test
    public void testGetNumberOfDistinctBookings01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	assertEquals(1, dm.getNumberOfDistinctBookings(LocalDate.now(), LocalDate.now()));
    }

    @Test
    public void testGetNumberOfDistinctBookings02() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final LocalDate startDate = LocalDate.now();
	final LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	assertEquals(1, dm.getNumberOfDistinctBookings(startDate, endDate));
    }

    @Test
    public void testGetNumberOfDistinctBookings03() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	LocalDate startDate = LocalDate.now();
	LocalDate endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	startDate = LocalDate.now().plusDays(10);
	endDate = startDate.plusDays(3);
	dm.add(startDate, endDate, bb);
	assertEquals(2, dm.getNumberOfDistinctBookings(LocalDate.now(), endDate));
    }

    @Test
    public void testIsCheckIn01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	assertTrue(bb.isCheckIn());
    }

    @Test
    public void testIsCheckIn02() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	dm.add(LocalDate.now().plusDays(1), new BookingBean("testGuest", "testRoom"));
	assertTrue(bb.isCheckIn());
    }

    @Test
    public void testIsCheckOut01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	assertTrue(bb.isCheckOut());
    }

    @Test
    public void testIsCheckOut02() throws OverbookingException {
	dm.add(LocalDate.now(), new BookingBean("testGuest", "testRoom"));
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now().plusDays(1), bb);
	assertTrue(bb.isCheckOut());
    }

    @Test
    public void testIsConnected01() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	dm.add(LocalDate.now().plusDays(1), bb2);
	assertTrue(bb.isConnected(bb2));
	assertTrue(bb2.isConnected(bb));
    }

    @Test
    public void testIsConnected02() throws OverbookingException {
	final BookingBean bb = new BookingBean("testGuest", "testRoom");
	final BookingBean bb2 = new BookingBean("testGuest", "testRoom");
	dm.add(LocalDate.now(), bb);
	dm.add(LocalDate.now().plusDays(2), bb2);
	assertFalse(bb.isConnected(bb2));
	assertFalse(bb2.isConnected(bb));
    }

}

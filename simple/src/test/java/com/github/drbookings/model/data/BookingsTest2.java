package com.github.drbookings.model.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.CleaningEntry;

public class BookingsTest2 {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	final XMLStorage storage = new XMLStorage();
	final DataStore store = storage.load(new File("src" + File.separator + "test" + File.separator + "resources"
		+ File.separator + "test-data-2018-05.xml"));
	bookings = DataStore.transform(store.getBookingsSer());
	cleanings = DataStore.transformCleanings(store.getCleaningsSer());
    }

    @After
    public void tearDown() throws Exception {
    }

    private List<BookingBean> bookings;
    private List<CleaningEntry> cleanings;

    @Test
    public void testNumberOfNights01() {

	assertThat(Bookings.countNights(new BookingsByOrigin<>(bookings).getAirbnbBookings(), YearMonth.of(2018, 05)),
		is(4 + 5 + 5 + 31L));
    }

    @Test
    public void testNumberOfNights02() {

    }

    @Test
    public void testNumberOfNights03() {

    }

    @Test
    public void testNumberOfNights04() {

    }

}

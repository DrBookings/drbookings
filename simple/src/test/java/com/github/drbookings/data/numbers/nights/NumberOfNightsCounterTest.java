package com.github.drbookings.data.numbers.nights;

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

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.BookingsByOrigin;

public class NumberOfNightsCounterTest {

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

    }

    @After
    public void tearDown() throws Exception {
	bookings = null;
    }

    private List<BookingBean> bookings;

    @Test
    public void test2018MaiBooking01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = NumberOfNightsCounter.countNights(bbo.getBookingBookings(), YearMonth.of(2018, 05));
	assertThat(numberOfNights, is(64L));
    }

    @Test
    public void test2018MaiAirbnb01() {
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(bookings);
	final long numberOfNights = NumberOfNightsCounter.countNights(bbo.getAirbnbBookings(), YearMonth.of(2018, 05));
	assertThat(numberOfNights, is(4 + 5 + 5 + 31L));
    }

}

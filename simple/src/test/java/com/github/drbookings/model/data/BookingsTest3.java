package com.github.drbookings.model.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;

public class BookingsTest3 {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 10, 15));
    }

    @After
    public void tearDown() throws Exception {
	bb = null;
    }

    private BookingBean bb;

    @Test
    public void testNumberOfNights01() {

	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 05)), is(16L));
    }

    @Test
    public void testNumberOfNights02() {

	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 06)), is(30L));
    }

    @Test
    public void testNumberOfNights03() {

	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 06)), is(30L));
    }

}

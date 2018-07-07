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

public class BookingsTest {

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
    public void testNumberOfNights01() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 20));
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 05)), is(5L));
    }

    @Test
    public void testNumberOfNights02() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 20));
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 06)), is(0L));
    }

    @Test
    public void testNumberOfNights03() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 30), LocalDate.of(2015, 06, 02));
	assertThat(Bookings.countNights(Arrays.asList(bb)), is(3L));
    }

    @Test
    public void testNumberOfNights04() {
	final BookingBean bb = TestUtils.getTestBooking(LocalDate.of(2015, 05, 30), LocalDate.of(2015, 06, 02));
	assertThat(Bookings.countNights(Arrays.asList(bb), YearMonth.of(2015, 05)), is(1L));
    }

}

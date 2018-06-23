package com.github.drbookings.data.numbers.nights;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.data.numbers.Numbers;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.ui.BookingsByOrigin;

public class NightsPercentProviderTest {

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
	p = null;
    }

    private NightsPercentProvider p;

    @Test
    public void test01() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(Arrays.asList(b1, b2));
	assertThat(NightsPercentProvider.countNights(new BookingOrigin("booking"), bbo.getAllBookings()), is(8L));

    }

    @Test
    public void test02() {
	final BookingBean o1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 19),
		new BookingOrigin(""));
	final BookingBean a2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 16), LocalDate.of(2015, 05, 20),
		new BookingOrigin("airbnb"));
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingBean c1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 22),
		new BookingOrigin("booking"));
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(Arrays.asList(o1, a2, b1, c1));
	p = new NightsPercentProvider(bbo);
	// without cheating, its 1/4 of bookings
	assertThat(p.getPercent("airbnb", false), is(Numbers.getDefault(1 / 4d)));
	// with cheating, it is only 1/3
	assertThat(p.getPercent("airbnb", true), is(Numbers.getDefault(1 / 3d)));

	assertThat(p.getPercent("booking", false), is(Numbers.getDefault(1 / 2d)));

	assertThat(p.getPercent("booking", true), is(Numbers.getDefault(2 / 3d)));
    }

}

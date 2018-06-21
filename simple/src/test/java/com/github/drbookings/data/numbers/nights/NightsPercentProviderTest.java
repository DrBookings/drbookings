package com.github.drbookings.data.numbers.nights;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
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
	final BookingBean o1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		new BookingOrigin(""));
	final BookingBean a2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 16), LocalDate.of(2015, 05, 19),
		new BookingOrigin("airbnb"));
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 18), LocalDate.of(2015, 05, 21),
		new BookingOrigin("booking"));
	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(Arrays.asList(o1, a2, b1));
	p = new NightsPercentProvider(bbo);
	System.err.println(p.getPercent("airbnb", false));
	System.err.println(p.getPercent("airbnb", true));
	System.err.println(p.getPercent("booking", false));
	System.err.println(p.getPercent("booking", true));
    }

}

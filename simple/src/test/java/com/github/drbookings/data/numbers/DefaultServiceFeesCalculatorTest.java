package com.github.drbookings.data.numbers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.data.BookingBean;

public class DefaultServiceFeesCalculatorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	c = new DefaultServiceFeesCalculator();
    }

    @After
    public void tearDown() throws Exception {
	c = null;
    }

    private DefaultServiceFeesCalculator c;

    @Test
    public void test01() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 10, 12), LocalDate.of(2015, 10, 15));
	final Number n = c.apply(b);
	assertThat(n.doubleValue(), is(0d));
    }

    @Test
    public void test02() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 10, 12), LocalDate.of(2015, 10, 15));
	b.setServiceFeesPercent(12);
	final Number n = c.apply(b);
	assertThat(n.doubleValue(), is(0d));
    }

    @Test
    public void test03() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 10, 12), LocalDate.of(2015, 10, 15));
	b.setGrossEarningsExpression("100");
	b.setServiceFeesPercent(12);
	final Number n = c.apply(b);
	assertThat(n.doubleValue(), is(12d));
    }

}

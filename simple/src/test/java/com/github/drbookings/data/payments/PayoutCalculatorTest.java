package com.github.drbookings.data.payments;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.MathContext;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import javax.money.MonetaryAmount;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.PaymentImpl;
import com.github.drbookings.model.data.BookingBean;

public class PayoutCalculatorTest {

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
	pc = null;
    }

    private PayoutCalculator pc;

    @Test
    public void test01() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"booking");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final List<BookingBean> bookings = Arrays.asList(b1, b2);
	final MonetaryAmount fixCosts = Payments.createMondary(0);

	pc = new PayoutCalculator(YearMonth.of(2015, 05), bookings, Arrays.asList());
	assertThat(pc.getPayout("booking", fixCosts, false).getNumber().doubleValue(), is(200d));
    }

    @Test
    public void test02() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final List<BookingBean> bookings = Arrays.asList(b1, b2);

	final MonetaryAmount fixCosts = Payments.createMondary(0);

	pc = new PayoutCalculator(YearMonth.of(2015, 05), bookings, Arrays.asList());
	assertThat(pc.getPayout("booking", fixCosts, false).getNumber().doubleValue(), is(100d));
    }

    @Test
    public void test03() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b3 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b3.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final List<BookingBean> bookings = Arrays.asList(b1, b2, b3);

	final MonetaryAmount fixCosts = Payments.createMondary(0);

	pc = new PayoutCalculator(YearMonth.of(2015, 05), bookings, Arrays.asList());
	assertThat(pc.getPayout("booking", fixCosts, false).getNumber().round(MathContext.DECIMAL32).doubleValue(),
		is(100d));
    }

    @Test
    public void test04() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));

	final List<BookingBean> bookings = Arrays.asList(b1, b2);

	final MonetaryAmount fixCosts = Payments.createMondary(20);

	pc = new PayoutCalculator(YearMonth.of(2015, 05), bookings, Arrays.asList());
	assertThat(pc.getPayout("booking", fixCosts, false).getNumber().round(MathContext.DECIMAL32).doubleValue(),
		is(100d - 20d / 2d));
    }

}

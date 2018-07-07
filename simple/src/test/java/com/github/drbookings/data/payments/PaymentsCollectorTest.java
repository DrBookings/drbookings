package com.github.drbookings.data.payments;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.Payment;
import com.github.drbookings.model.PaymentImpl;
import com.github.drbookings.model.data.BookingBean;

public class PaymentsCollectorTest {

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
    public void test01() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15));
	final PaymentImpl p = new PaymentImpl(LocalDate.of(2015, 05, 15), 120);
	b.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 15), 120));
	final List<Payment> payments = new PaymentsCollector(YearMonth.of(2015, 05)).collect(Arrays.asList(b));
	assertThat(payments.size(), is(1));
	assertThat(payments.get(0), is(p));
    }

}

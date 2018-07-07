package com.github.drbookings.data.payments;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.time.YearMonth;
import java.util.List;

import javax.money.MonetaryAmount;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.CleaningExpense;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;

public class PayoutCalculatorTest2 {

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
	pc = null;
	bookings = null;

    }

    private PayoutCalculator pc;

    private List<BookingBean> bookings;

    @Test
    public void test01() {

	final MonetaryAmount fixCosts = Payments.createMondary(0);

	pc = new PayoutCalculator(YearMonth.of(2018, 05), bookings,
		CleaningExpense.build(TestUtils.getCleanings2018Mai()));
	assertThat(pc.getPayout("Booking", fixCosts, false).getNumber().doubleValue(), is(200d));
    }

    @Test
    public void test02() {
	final MonetaryAmount fixCosts = Payments.createMondary(0);

	pc = new PayoutCalculator(YearMonth.of(2018, 05), bookings,
		CleaningExpense.build(TestUtils.getCleanings2018Mai()));
	assertThat(pc.getPayout("Airbnb", fixCosts, false).getNumber().doubleValue(), is(952d));
    }

    @Test
    public void test03() {

    }

    @Test
    public void test04() {

    }

}

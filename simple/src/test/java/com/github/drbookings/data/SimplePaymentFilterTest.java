package com.github.drbookings.data;

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

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.data.Bookings;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;

public class SimplePaymentFilterTest {

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
		+ File.separator + "test-data-2018-05-payment-filter-01.xml"));
	bbo = Bookings.toEntries(DataStore.transform(store.getBookingsSer()));
    }

    @After
    public void tearDown() throws Exception {
	bbo = null;
    }

    private List<BookingEntry> bbo;

    @Test
    public void test01() throws Exception {

	// wrong date
	final long cnt = bbo.stream().filter(new SimplePaymentFilter(YearMonth.of(2017, 05))).count();
	assertThat(cnt, is(0l));

    }

    @Test
    public void test02() throws Exception {

	final long cnt = bbo.stream().filter(new SimplePaymentFilter(YearMonth.of(2018, 05))).count();
	assertThat(cnt, is(31l));

    }

}

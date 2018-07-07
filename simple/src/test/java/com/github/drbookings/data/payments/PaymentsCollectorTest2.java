package com.github.drbookings.data.payments;

import java.io.File;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;

public class PaymentsCollectorTest2 {

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

    private List<BookingBean> bookings;

    private PaymentsCollector pc;

    @Test
    public void test01() {

    }

}

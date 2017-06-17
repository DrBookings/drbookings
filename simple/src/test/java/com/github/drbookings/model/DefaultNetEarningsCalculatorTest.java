package com.github.drbookings.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.ui.CleaningEntry;

public class DefaultNetEarningsCalculatorTest {

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
	public void test() {
		final DefaultNetEarningsCalculator c = new DefaultNetEarningsCalculator();
		final Booking b = TestUtils.getTestBooking(LocalDate.now(), LocalDate.now().plusDays(4));
		b.setGrossEarningsExpression("360");
		b.setServiceFee(0);
		b.setServiceFeesPercent(12f);
		b.setCleaningFees(60);
		final CleaningEntry ce = new CleaningEntry(LocalDate.now(), b, new Cleaning("testCleaning"), null);
		ce.setCleaningCosts(40);
		b.setCleaning(ce);
		assertEquals(284, c.apply(b).doubleValue(), 0.001);

	}

}

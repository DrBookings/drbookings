package com.github.drbookings.model.data.manager;

import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.TestUtils;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.ui.CleaningEntry;

public class MainManagerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	private MainManager mm;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddCleaning01() {
		mm = new MainManager();
		final CleaningEntry ce = new CleaningEntry(LocalDate.now(), TestUtils.getTestBooking(),
				new Cleaning("testCleaning"), null);
		mm.addCleaning(LocalDate.now(), "testCleaning", TestUtils.getTestBooking());
		assertTrue(mm.getCleaningEntries().contains(ce));
	}

}

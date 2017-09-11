package com.github.drbookings.model.data.manager;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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
		final CleaningEntry ce = new CleaningEntry(LocalDate.now(), TestUtils.getTestBooking("tt"),
				new Cleaning("testCleaning"), null);
		mm.addCleaning(LocalDate.now(), "testCleaning", TestUtils.getTestBooking("tt"));
		assertTrue(mm.getCleaningEntries().contains(ce));
	}

}

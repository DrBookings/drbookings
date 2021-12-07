/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
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
 */

package com.github.drbookings.ui;

import com.github.drbookings.CleaningEntry;
import com.github.drbookings.CleaningEntryFactory;
import org.junit.*;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class CleaningEntryFactoryTest {

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
	factory = null;
    }

    private CleaningEntryFactory factory;

    @Test
    public void test00() {
	factory = new CleaningEntryFactory();
    }

    @Test
    public void test01() {
	factory = new CleaningEntryFactory();
	CleaningEntry ce = factory.createCleaning(LocalDate.now(), "Alona", "F4", 25, true);
	assertThat(ce, is(not(nullValue())));
	assertThat(ce.getName(), is("Alona"));
	assertThat(ce.getRoom().getName(), is("F4"));
	assertThat(ce.getCleaningCosts(), is(25f));
	assertThat(ce.isBlack(), is(true));
    }

}

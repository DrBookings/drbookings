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

package com.github.drbookings.model.data;

import com.github.drbookings.CleaningEntry;
import com.github.drbookings.data.History;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import org.junit.*;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DataStoreIOTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	io = new FromXMLReader();
    }

    @After
    public void tearDown() throws Exception {
	io = null;
    }

    private static final File testFile = new File("src" + File.separator + "test" + File.separator + "resources"
	    + File.separator + DataStoreIOTest.class.getSimpleName() + ".xml");

    private FromXMLReader io;

    @Test
    public void test01() throws Exception {

	final List<CleaningEntry> expenses = History.getCleanings2018Mai();

	final DataStoreCoreSer ds = new DataStoreCoreSer();

	io.writeToFile(ds, testFile);

	final DataStoreCoreSer store = io.readFromFile(testFile);
	assertThat(store, is(ds));

    }

}

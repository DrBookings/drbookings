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

package com.github.drbookings.model.data.manager;

import com.github.drbookings.Room;
import com.github.drbookings.RoomFactory;
import org.junit.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;

public class RoomFactoryTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	f = RoomFactory.getInstance();
    }

    @After
    public void tearDown() throws Exception {
	f = null;
    }

    private RoomFactory f;

    @Test
    public void test01() {
	final Room r = f.getOrCreateElement("r1");
	assertThat(r.getName(), is("r1"));
    }

    @Test
    public void test02() {
	final Room r = f.getOrCreateElement("r1");
	assertThat(r.getId(), is("r1"));
    }

    @Test
    public void test03() {
	final Room r1 = f.getOrCreateElement("r1");
	final Room r2 = f.getOrCreateElement("r1");
	assertThat(r1, sameInstance(r2));
    }

}

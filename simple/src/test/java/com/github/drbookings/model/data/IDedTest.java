package com.github.drbookings.model.data;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class IDedTest {

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
		i = null;
		j = null;
	}

	private IDed i;

	private IDed j;

	@Test
	public void test01() {
		i = new IDed();
		assertNotNull(i.getId());
		assertFalse(StringUtils.isBlank(i.getId()));
	}

	@Test
	public void test02() {
		i = new IDed("dd");
		assertNotNull(i.getId());
		assertEquals("dd", i.getId());
	}

	@Test
	public void testIdentity01() {
		i = new IDed();
		j = new IDed();
		assertNotEquals(i.hashCode(), j.hashCode());
		assertNotEquals(i, j);
	}

	@Test
	public void testIdentity03() {
		i = new IDed();
		final Object o = new Object();
		assertNotEquals(i.hashCode(), o.hashCode());
		assertNotEquals(i, o);
	}

	@Test
	public void testIdentity02() {
		i = new IDed("dd");
		j = new IDed("dd");
		assertEquals(i.hashCode(), j.hashCode());
		assertEquals(i, j);
	}

}

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

import com.github.drbookings.*;
import org.junit.Test;
import org.junit.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CleaningExpensesFactoryTest {

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
    public void testBuild01() {
	final CleaningEntry ce = TestUtils.getTestCleaningEntry(new DummyBooking());
	ce.setCleaningCosts(40f);
	final List<CleaningExpense> exps = CleaningExpensesFactory.build(Arrays.asList(ce), false);
	assertThat(exps.size(), is(1));
	final CleaningExpense exp = exps.iterator().next();
	assertThat(exp.getAmount(), is(Payments.createMondary(40)));
    }

    @Test
    public void testBuild02() {
	final CleaningEntry ce = TestUtils.getTestCleaningEntry(new DummyBooking());
	ce.setCleaningCosts(40f);
	final List<CleaningExpense> exps = CleaningExpensesFactory.build(Arrays.asList(ce), true);
	assertThat(exps.size(), is(1));
	final CleaningExpense exp = exps.iterator().next();
	assertThat(exp.getAmount(), is(Payments.createMondary(40)));
    }

}

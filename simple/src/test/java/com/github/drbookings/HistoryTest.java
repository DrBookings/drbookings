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

package com.github.drbookings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.data.History;

public class HistoryTest {

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
    public void testCommonExpenses2018Mai01() {
	assertThat(Payments.getSum(History.getCommonExpenses2018Mai()), is(Payments.createMondary(2897.17)));
    }

    @Test
    public void testCommonExpenses2018June01() {
	assertThat(Payments.getSum(History.getCommonExpenses2018June()), is(Payments.createMondary(2048.02)));
    }

    @Test
    public void testCommonExpenses2018Junly01() {
	assertThat(Payments.getSum(History.getCommonExpenses2018July()), is(Payments.createMondary(2083.07)));
    }

}

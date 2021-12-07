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

import javax.money.Monetary;
import javax.money.MonetaryAmount;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MoneyTest {

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

    static MonetaryAmount create(final double amount) {
	return Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(amount).create();
    }

    @Test
    public void test() {
	MonetaryAmount sum = Monetary.getDefaultAmountFactory().setCurrency("EUR").setNumber(0).create();
	sum = sum.add(create(1.8));
	sum = sum.add(create(42.57));
	sum = sum.add(create(154.35));
	sum = sum.add(create(43.98));
	sum = sum.add(create(26.98));
	sum = sum.add(create(8.1));
	sum = sum.add(create(96.99));
	sum = sum.add(create(116.03));
	sum = sum.add(create(42.5));
	sum = sum.add(create(209.98));
	sum = sum.add(create(21.24));
	sum = sum.add(create(38.85));
	sum = sum.add(create(43.74));
	sum = sum.add(create(43.74));
	sum = sum.add(create(39.95));
	sum = sum.add(create(45.63));
	sum = sum.add(create(1500.0));
	sum = sum.add(create(7.95));
	sum = sum.add(create(22.88));
	sum = sum.add(create(15.03));
	sum = sum.add(create(18.6));
	sum = sum.add(create(180));
	sum = sum.add(create(31.29));
	sum = sum.add(create(144.99));
	assertThat(sum, is(Payments.createMondary("2897.17")));
    }

}

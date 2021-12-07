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

package com.github.drbookings.model;

import com.github.drbookings.AirbnbEarningsCalculator;
import com.github.drbookings.BookingBean;
import com.github.drbookings.PaymentImpl;
import com.github.drbookings.model.data.BookingBeanTest;
import org.junit.*;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class AirbnbEarningsCalculatorTest {

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    private AirbnbEarningsCalculator c;

    @Before
    public void setUp() {
	c = new AirbnbEarningsCalculator().filterForPaymentDone(false);
    }

    @After
    public void tearDown() {
	c = null;
    }

    @Test
    public void test01() {
	final BookingBean b = BookingBeanTest.newInstance(LocalDate.of(2012, 02, 02), LocalDate.of(2012, 2, 5));
	b.setPaymentDone(true);
	final float result = c.calculateEarnings(b);
	assertThat(result, is(0f));
    }

    @Test
    public void test02() {
	final BookingBean b = BookingBeanTest.newInstance(LocalDate.of(2012, 02, 02), LocalDate.of(2012, 2, 5));
	b.setPaymentDone(true);
	b.setGrossEarningsExpression("30");
	final float result = c.calculateEarnings(b);
	assertThat(result, is(30f));
    }

    @Test
    public void test03() {
	final BookingBean b = BookingBeanTest.newInstance(LocalDate.of(2018, 01, 27), LocalDate.of(2018, 8, 5));
	b.setPaymentDone(true);
	b.setGrossEarningsExpression("8252.86");
	b.getPayments().add(new PaymentImpl(LocalDate.of(2018, 01, 29), 1300));
	assertThat(b.getNumberOfNights(), is(190));
	final float result = c.calculateEarnings(b);
	assertThat(result, is(1300f));
    }
}
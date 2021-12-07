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

package com.github.drbookings.data.numbers;

import com.github.drbookings.BookingBean;
import com.github.drbookings.DefaultServiceFeesSupplier;
import com.github.drbookings.Payments;
import com.github.drbookings.TestUtils;
import org.junit.*;

import javax.money.MonetaryAmount;
import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DefaultServiceFeesCalculatorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	c = new DefaultServiceFeesSupplier();
    }

    @After
    public void tearDown() throws Exception {
	c = null;
    }

    private DefaultServiceFeesSupplier c;

    @Test
    public void test01() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 10, 12), LocalDate.of(2015, 10, 15));
	final MonetaryAmount n = c.apply(b);
	assertThat(n, is(Payments.createMondary(0)));
    }

    @Test
    public void test02() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 10, 12), LocalDate.of(2015, 10, 15));
	b.setServiceFeesPercent(12);
	final MonetaryAmount n = c.apply(b);
	assertThat(n, is(Payments.createMondary(0)));
    }

    @Test
    public void test03() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 10, 12), LocalDate.of(2015, 10, 15));
	b.setGrossEarningsExpression("100");
	b.setServiceFeesPercent(12);
	final MonetaryAmount n = c.apply(b);
	assertThat(n, is(Payments.createMondary(12)));
    }

}

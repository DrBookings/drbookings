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

package com.github.drbookings.data.payment;

import com.github.drbookings.*;
import org.junit.Test;
import org.junit.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PaymentsCollectorTest {

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
    public void test01() {
	final BookingBean b = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15));
	final PaymentImpl p = new PaymentImpl(LocalDate.of(2015, 05, 15), 120);
	b.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 15), 120));
	final List<Payment> payments = new PaymentsCollector(YearMonth.of(2015, 05)).collect(Arrays.asList(b));
	assertThat(payments.size(), is(1));
	assertThat(payments.get(0), is(p));
    }

}

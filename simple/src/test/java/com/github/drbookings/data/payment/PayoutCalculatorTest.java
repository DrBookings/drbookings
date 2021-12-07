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

import javax.money.Monetary;
import java.math.MathContext;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PayoutCalculatorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	expenses = new ArrayList<>();
    }

    @After
    public void tearDown() throws Exception {
	pc = null;
	expenses = null;
    }

    private DefaultPayoutSupplier pc;

    private Collection<ExpenseBean> expenses;

    @Test
    public void testPayoutFactor01() {
	final String amount = "100";
	final float payout = 0.9f;
	assertThat(DefaultPayoutSupplier.applyPayoutFactor(amount, payout).with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(90)));
    }

    @Test
    public void testPayoutFactor02() {
	final String amount = "-100";
	final float payout = 0.9f;
	assertThat(DefaultPayoutSupplier.applyPayoutFactor(amount, payout).with(Monetary.getDefaultRounding()),
		is(Payments.createMondary(-90)));
    }

    @Test
    public void test01() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"booking");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final List<BookingBean> bookings = Arrays.asList(b1, b2);

	pc = new DefaultPayoutSupplier(YearMonth.of(2015, 05), bookings, Arrays.asList(), expenses);
	assertThat(pc.getPayout("booking", false).getNumber().doubleValue(), is(200d));
    }

    @Test
    public void test02() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final List<BookingBean> bookings = Arrays.asList(b1, b2);

	pc = new DefaultPayoutSupplier(YearMonth.of(2015, 05), bookings, Arrays.asList(), expenses);
	assertThat(pc.getPayout("booking", false).getNumber().doubleValue(), is(100d));
    }

    @Test
    public void test03() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b3 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b3.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final List<BookingBean> bookings = Arrays.asList(b1, b2, b3);

	pc = new DefaultPayoutSupplier(YearMonth.of(2015, 05), bookings, Arrays.asList(), expenses);
	assertThat(pc.getPayout("booking", false).getNumber().round(MathContext.DECIMAL32).doubleValue(), is(100d));
    }

    @Test
    public void test04() {
	final BookingBean b1 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 12), LocalDate.of(2015, 05, 15),
		"booking");
	b1.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));
	final BookingBean b2 = TestUtils.getTestBooking(LocalDate.of(2015, 05, 15), LocalDate.of(2015, 05, 18),
		"airbnb");
	b2.getPayments().add(new PaymentImpl(LocalDate.of(2015, 05, 30), 100));

	final List<BookingBean> bookings = Arrays.asList(b1, b2);

	pc = new DefaultPayoutSupplier(YearMonth.of(2015, 05), bookings, Arrays.asList(), expenses);
	assertThat(pc.getPayout("booking", false).getNumber().round(MathContext.DECIMAL32).doubleValue(), is(100d));
    }

}

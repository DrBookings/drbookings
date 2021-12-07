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
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreFactory;
import org.junit.Test;
import org.junit.*;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.io.File;
import java.time.YearMonth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class PaymentsTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {

	data = DataStoreFactory.build(new FromXMLReader().readFromFile(new File("src" + File.separator + "test"
		+ File.separator + "resources" + File.separator + "test-data-2018-05_2.xml")));

    }

    @After
    public void tearDown() throws Exception {
	data = null;
    }

    private DataStoreCore data;

    @Test
    public void test2018MaiPaymentSumBooking01() {

	final BookingsByOrigin<BookingBean> bbo = new BookingsByOrigin<>(data.getBookings());
	final MonetaryAmount paymentSum = Payments.getSum(bbo.getByOrigin("Booking"), YearMonth.of(2018, 05));
	assertThat(paymentSum.with(Monetary.getDefaultRounding()), is(Payments.createMondary("4038.95")));
    }

    @Test
    public void test2018MaiCommonExpenses01() {
	final MonetaryAmount commonExpenses = Payments
		.getSum(Expenses.getCommonExpenses(data.getExpensesIncludingCleaning(false)));

	assertThat(commonExpenses.with(Monetary.getDefaultRounding()), is(Payments.createMondary("2897.17")));

    }

    @Test
    public void test2018MaiCommonExpenses02() {
	final MonetaryAmount commonExpenses = Payments
		.getSum(Expenses.getCommonExpenses(data.getExpensesIncludingCleaning(true)));

	// nothing changed
	assertThat(commonExpenses.with(Monetary.getDefaultRounding()), is(Payments.createMondary("2897.17")));

    }

    @Test
    public void test2018MaiAssingedExpenses01() {
	final MonetaryAmount commonExpenses = Payments
		.getSum(Expenses.getAssignedExpenses(data.getExpensesIncludingCleaning(true), "Booking"));
	// System.err.println(Expenses.getAssignedExpenses(data.getExpensesIncludingCleaning(true),
	// "Booking").stream()
	// .map(Object::toString).collect(Collectors.joining("\n")));
	assertThat(commonExpenses.with(Monetary.getDefaultRounding()), is(Payments.createMondary(580 - 25 * 4)));

    }

}

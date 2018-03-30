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

import org.junit.*;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class BookingBeanTest {

    private BookingBean b1, b2;

    @BeforeClass
    public static void setUpBeforeClass() {
    }

    @AfterClass
    public static void tearDownAfterClass() {
    }

    public static BookingBean newInstance() {
        return newInstance(
                LocalDate.of(2017, 06, 01), LocalDate.of(2017, 06, 04));
    }

    public static BookingBean newInstance(LocalDate checkin, LocalDate checkout) {
        return new BookingBean
                (new Guest("testGuest"), new Room("testRoom"), new BookingOrigin("testOrigin"),
                        checkin, checkout);
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        b1 = null;
        b2 = null;
    }

    @Test
    public void testNumberOfDaysAndNights01() {
        final BookingBean b = new BookingBean(new Guest("testGuest"), new Room("testRoom"), new BookingOrigin("testOrigin"),
                LocalDate.of(2017, 06, 01), LocalDate.of(2017, 07, 01));
        assertEquals(31, b.getNumberOfDays());
        assertEquals(30, b.getNumberOfNights());

    }

    @Test
    public void testDateOfPayment01() {
        final BookingBean b = newInstance();
        assertThat(b.isPaymentDone(), is(false));
        b.setPaymentDone(true);
        assertThat(b.isPaymentDone(), is(true));
    }

    @Test
    public void testDateOfPayment02() {
        final BookingBean b = newInstance();
        assertThat(b.isPaymentDone(), is(false));
        assertThat(b.getDateOfPayment(), is(nullValue()));
        b.setPaymentDone(true);
        assertThat(b.getDateOfPayment(), is(LocalDate.now()));
    }

    @Test
    public void testDateOfPayment03() {
        final BookingBean b = newInstance();
        assertThat(b.getDateOfPayment(), is(nullValue()));
        b.setDateOfPayment(LocalDate.of(2017, 12, 12));
        assertThat(b.getDateOfPayment(), is(LocalDate.of(2017, 12, 12)));
        assertThat(b.isPaymentDone(), is(true));
    }

    @Test
    public void testGrossEarnings01() {
        final BookingBean b = newInstance();
        b.setGrossEarningsExpression("30");
        assertThat(b.getGrossEarnings(), is(30f));
    }

    @Test
    public void testNetEarnings01() {
        final BookingBean b = newInstance();
        b.setGrossEarningsExpression("30");
        assertThat(b.getNetEarnings(), is(30f));
    }

}

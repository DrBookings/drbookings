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
import com.github.drbookings.DefaultTurnoverCalculator;
import com.github.drbookings.Payments;
import com.github.drbookings.data.History;
import org.junit.*;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.time.YearMonth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DefaultUmsatzCalculatorTest {

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
    public void testEugenio01() {
	final BookingBean eugenio = History.getEugenio();
	final MonetaryAmount gross = new DefaultTurnoverCalculator(YearMonth.of(2018, 01)).apply(eugenio);
	assertThat(gross, is(Payments.createMondary(0)));
    }

    @Test
    public void testEugenio02() {
	final BookingBean eugenio = History.getEugenio();
	final MonetaryAmount gross = new DefaultTurnoverCalculator(YearMonth.of(2018, 02)).apply(eugenio);
	assertThat(gross, is(Payments.createMondary(1366.87 + 1211.82).with(Monetary.getDefaultRounding())));
    }

    @Test
    public void testEugenio03() {
	final BookingBean eugenio = History.getEugenio();
	final MonetaryAmount gross = new DefaultTurnoverCalculator(YearMonth.of(2018, 03)).apply(eugenio);
	assertThat(gross, is(Payments.createMondary(1342.78).with(Monetary.getDefaultRounding())));
    }

}

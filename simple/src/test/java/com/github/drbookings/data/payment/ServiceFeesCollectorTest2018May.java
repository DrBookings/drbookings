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

import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.DataStoreCore;
import com.github.drbookings.Payment;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;
import org.junit.*;

import java.io.File;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ServiceFeesCollectorTest2018May {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
	dataSer = new FromXMLReader().readFromFile(testFile);
	data = DataStoreFactory.build(dataSer);
    }

    @After
    public void tearDown() throws Exception {
	data = null;
	dataSer = null;
    }

    private final static File testFile = new File("src" + File.separator + "test" + File.separator + "resources"
	    + File.separator + "test-data-2018-05_2.xml");

    private final static YearMonth month = YearMonth.of(2018, 05);

    private DataStoreCoreSer dataSer;

    private DataStoreCore data;

    @Test
    public void testBooking01() {
	final List<Payment> fees = new ServiceFeesCollector(month,
		new BookingsByOrigin<>(data.getBookings()).getBookingBookings()).collect();
	final List<Number> numbers = fees.stream().map(e -> e.getAmount().getNumber().doubleValue())
		.collect(Collectors.toList());

	assertThat(numbers.contains(15.66), is(true));
	assertThat(numbers.contains(24.36), is(true));
	assertThat(numbers.contains(14.256), is(true));
	assertThat(numbers.contains(33.48), is(true));
	assertThat(numbers.contains(15.84), is(true));
	assertThat(numbers.contains(15.228), is(true));
	assertThat(numbers.contains(28.44), is(true));
	assertThat(numbers.contains(63.72), is(true));
	assertThat(numbers.contains(15.066), is(true));

	assertThat(numbers.contains(22.464), is(true));
	assertThat(numbers.contains(28.08), is(true));
	// assertThat(numbers.contains(39.312), is(true)); Overbooking Juergen Drews
	assertThat(numbers.contains(16.92), is(true));
	assertThat(numbers.contains(16.92), is(true));
	assertThat(numbers.contains(15.84), is(true));
	assertThat(numbers.contains(24.192), is(true));
	assertThat(numbers.contains(19.008), is(true));

	assertThat(numbers.stream().mapToDouble(d -> d.doubleValue()).sum(),
		is(369.474 /* Juergen Drews Overbooking */));

    }

}
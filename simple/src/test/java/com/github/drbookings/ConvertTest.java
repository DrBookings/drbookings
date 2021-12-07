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

import com.github.drbookings.data.History;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.model.ser.BookingBeanSerFactory;
import com.github.drbookings.ser.BookingBeanSer;
import com.github.drbookings.ser.CleaningBeanSer;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.google.common.collect.Range;
import org.junit.Test;
import org.junit.*;

import java.io.File;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ConvertTest {

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

    private static final File testFile = new File("/home/alex/Dropbox/bookings (copy).xml");

    private static final YearMonth month = YearMonth.of(2018, 10);

    private static final String fileName = "test-data-2018-10_2";

    @Ignore
    @Test
    public void test() throws Exception {
	final Range<LocalDate> dateRange = LocalDates.toDateRange(month);
	final FromXMLReader io = new FromXMLReader();
	final DataStoreCoreSer dataSer = io.readFromFile(testFile);
	final BookingBeanFactory f = new BookingBeanFactory();

	final List<BookingBeanSer> bookings1 = dataSer.getBookingsSer().stream()
		.filter(e -> dateRange.contains(e.checkInDate) || dateRange.contains(e.checkOutDate)).sorted(Comparator
			.comparing(BookingBeanSer::getRoomName).thenComparing(BookingBeanSer::getCheckOutDate))
		.collect(Collectors.toList());
	final List<BookingBean> bookings2 = bookings1
		.stream().map(e -> f.createBooking(e)).sorted(Comparator.comparing(BookingBean::getBookingOrigin)
			.thenComparing(BookingBean::getRoom).thenComparing(BookingBean::getCheckOut))
		.collect(Collectors.toList());

	final CleaningEntryFactory fc = new CleaningEntryFactory(bookings2);
	fc.addModifier(e -> {
	    if (e.getName().equalsIgnoreCase("alona") || e.getName().equalsIgnoreCase("hanife")) {
		e.setBlack(true);
		// e.setCleaningCosts(25);
	    }
	});

	final List<CleaningBeanSer> cleanings1 = dataSer.getCleaningsSer().stream()
		.filter(e -> dateRange.contains(e.date)).collect(Collectors.toList());

	final List<CleaningEntry> cleanings2 = cleanings1.stream().map(e -> fc.createCleaning(e)).sorted()
		.collect(Collectors.toList());

	System.err.println(bookings1.stream().map(Object::toString).collect(Collectors.joining("\n")));
	System.err.println(cleanings1.stream().map(Object::toString).collect(Collectors.joining("\n")));
	System.err.println();
	final BookingsByOrigin<BookingBean> byo = new BookingsByOrigin<>(bookings2);

	for (final Map.Entry<BookingOrigin, Collection<BookingBean>> e : byo.getMap().entrySet()) {
	    System.err.println(e.getKey() + String.format("%4d", e.getValue().size()));
	}
	System.err.println();
	System.err.println(bookings2.stream().map(Object::toString).collect(Collectors.joining("\n")));
	System.err.println(cleanings2.stream().map(Object::toString).collect(Collectors.joining("\n")));

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(History.getCommonExpenses2018Sept());

	dataSer.setBookingSer(BookingBeanSerFactory.build(bookings2));
	dataSer.setCleaningSer(CleaningBeanSerFactory.build(cleanings2));
	dataSer.setExpenses(ExpenseSerFactory.build(expenses));

	io.writeToFile(dataSer, new File(
		"src" + File.separator + "test" + File.separator + "resources" + File.separator + fileName + ".xml"));

	// assertThat(bookings2.size(), is(21));
	// assertThat(cleanings2.size(), is(20));
    }

}

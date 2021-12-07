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

package com.github.drbookings.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingOrigin;
import com.github.drbookings.Cleaning;
import com.github.drbookings.CleaningEntry;
import com.github.drbookings.ExpenseBean;
import com.github.drbookings.Guest;
import com.github.drbookings.PaymentImpl;
import com.github.drbookings.Room;

public class History {

    @Deprecated
    public static final String DEFAULT_2018_FIX_COSTS_PER_MONTH_PER_ROOM = "375+45+((42,57+177,13)/4)";

    public static List<CleaningEntry> getCleanings2018Mai() {
	final List<CleaningEntry> cleanings = Arrays.asList(
		new CleaningEntry(LocalDate.of(2018, 05, 07), "Booking", new Cleaning("Hanife"), false, 50),
		new CleaningEntry(LocalDate.of(2018, 05, 07), "Booking", new Cleaning("Hanife"), false, 50));

	return cleanings;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018August() {

	/*
	 * Nurnoch laufende Kosten. Was unterscheidet eine mehrere-tausend-Euro
	 * Investition von neuen Glästern?
	 */

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 8, 31), 0.45),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 8, 31), 42.57),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 8, 31), 154.35),
		//
		new ExpenseBean("KNAPPSCHAFT-BAHN-SEE", LocalDate.of(2018, 8, 16), 30.16),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 8, 10), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 07, 5), 4 * 45)
	//
	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018Sept() {

	/*
	 * Nurnoch laufende Kosten. Was unterscheidet eine mehrere-tausend-Euro
	 * Investition von neuen Glästern?
	 */

	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 9, 28), 0.27),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 9, 28), 6.57),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 9, 28), 154.35),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 9, 10), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 9, 5), 4 * 45)
	//
	));

	return expenses;
    }

    public static Collection<? extends ExpenseBean> getCommonExpenses2018July() {
	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 07, 31), 1.08),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 07, 31), 42.57),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 07, 31), 154.35),
		//
		new ExpenseBean("Auslage Amazon", LocalDate.of(2018, 07, 27), 13.95),
		new ExpenseBean("Auslage Amazon", LocalDate.of(2018, 07, 27), 38.82),
		new ExpenseBean("Auslage Amazon", LocalDate.of(2018, 07, 27), 39.89),
		new ExpenseBean("Auslage Amazon", LocalDate.of(2018, 07, 27), 16.94),
		new ExpenseBean("Auslage Amazon", LocalDate.of(2018, 07, 27), 20),

		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 07, 10), 4 * 375),

		//
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 07, 9), 33.9),
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 07, 9), 10.28),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 07, 5), 4 * 45),
		//
		new ExpenseBean("KNAPPSCHAFT-BAHN-SEE", LocalDate.of(2018, 07, 2), 31.29)));

	return expenses;
    }

    public static List<ExpenseBean> getCommonExpenses2018June() {
	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 06, 29), 0.72),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 06, 29), 42.57),
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 06, 29), 154.35),
		//
		new ExpenseBean("Auslage Aldi", LocalDate.of(2018, 06, 25), 19.98),
		new ExpenseBean("Auslage Aldi", LocalDate.of(2018, 06, 25), 2.19),
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 06, 25), 71.80),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 06, 18), 4 * 375),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 06, 5), 4 * 45),
		//
		new ExpenseBean("Richard Müller GmbH", LocalDate.of(2018, 06, 4), 45.12),
		//
		new ExpenseBean("KNAPPSCHAFT-BAHN-SEE", LocalDate.of(2018, 06, 1), 31.29)));
	// expenses.addAll(getCommonFixCosts2018(LocalDate.of(2018, 05, 15)));

	// System.err.println(expenses.stream().sorted((e1, e2) ->
	// e2.getAmount().compareTo(e1.getAmount()))
	// .map(Object::toString).collect(Collectors.joining("\n")));

	return expenses;
    }

    public static List<ExpenseBean> getCommonExpenses2018Mai() {
	final List<ExpenseBean> expenses = new ArrayList<>();
	expenses.addAll(Arrays.asList(new ExpenseBean("Kontoführung", LocalDate.of(2018, 05, 31), 1.80),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 05, 30), 154.35),
		//
		new ExpenseBean("UNITYMEDIA BW GMBH", LocalDate.of(2018, 05, 28), 42.57),
		new ExpenseBean("Auslage Aldi", LocalDate.of(2018, 05, 28), 43.98),
		new ExpenseBean("Umbuchung Auslage", LocalDate.of(2018, 05, 28), 26.98),
		new ExpenseBean("Umbuchung Auslage", LocalDate.of(2018, 05, 28), 8.10),
		new ExpenseBean("Umbuchung", LocalDate.of(2018, 05, 28), 96.99),
		new ExpenseBean("Christian Heid", LocalDate.of(2018, 05, 28), 116.03),
		//
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 05, 21), 42.5),
		new ExpenseBean("Auslage Saturn", LocalDate.of(2018, 05, 21), 209.98),
		new ExpenseBean("Auslage Aldi", LocalDate.of(2018, 05, 21), 21.24),
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 05, 21), 38.85),
		//
		new ExpenseBean("Grundsteuer Bergheimer Straße 36/1 Whg1", LocalDate.of(2018, 05, 18), 43.74),
		new ExpenseBean("Grundsteuer Bergheimer Straße 36/1 Whg4", LocalDate.of(2018, 05, 18), 43.74),
		new ExpenseBean("Grundsteuer Bergheimer Straße 36/1 Whg3", LocalDate.of(2018, 05, 18), 39.95),
		new ExpenseBean("Grundsteuer Bergheimer Straße 36/1 Whg2", LocalDate.of(2018, 05, 18), 45.63),
		//
		new ExpenseBean("WEG Bergheimer Str. 36/1", LocalDate.of(2018, 05, 10), 4 * 375),
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 05, 10), 7.95),
		new ExpenseBean("Auslage Aldi", LocalDate.of(2018, 05, 10), 22.88),
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 05, 10), 15.03),
		//
		new ExpenseBean("Auslage Bauhaus", LocalDate.of(2018, 05, 8), 18.60),
		new ExpenseBean("KNAPPSCHAFT-BAHN-SEE", LocalDate.of(2018, 05, 8), 31.29),
		//
		new ExpenseBean("STADWERKE HEIDELBERG", LocalDate.of(2018, 05, 7), 4 * 45),
		//
		new ExpenseBean("Auslage WLAN Powerline", LocalDate.of(2018, 05, 2), 144.99)));
	// expenses.addAll(getCommonFixCosts2018(LocalDate.of(2018, 05, 15)));

	// System.err.println(expenses.stream().sorted((e1, e2) ->
	// e2.getAmount().compareTo(e1.getAmount()))
	// .map(Object::toString).collect(Collectors.joining("\n")));

	return expenses;
    }

    public static BookingBean getEugenio() {
	final BookingBean eugenio = new BookingBean(new Guest("eugenio"), new Room("2"), new BookingOrigin("Airbnb"),
		LocalDate.of(2018, 01, 27), LocalDate.of(2018, 05, 31));

	eugenio.setDateOfPayment(LocalDate.of(2018, 03, 03));

	eugenio.getPayments().add(new PaymentImpl(LocalDate.of(2018, 2, 2), 1366.87));
	eugenio.getPayments().add(new PaymentImpl(LocalDate.of(2018, 2, 28), 1211.82));
	eugenio.getPayments().add(new PaymentImpl(LocalDate.of(2018, 3, 31), 1342.78));
	eugenio.getPayments().add(new PaymentImpl(LocalDate.of(2018, 4, 30), 1299.42));
	eugenio.getPayments().add(new PaymentImpl(LocalDate.of(2018, 5, 28), 173.79));
	eugenio.getPayments().add(new PaymentImpl(LocalDate.of(2018, 5, 29), 37.41));
	return eugenio;
    }

}

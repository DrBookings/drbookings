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

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class MoneyMonitor {

	private static class InstanceHolder {
		private static final MoneyMonitor instance = new MoneyMonitor();
	}

	private class SelectionListener implements ListChangeListener<RoomBean> {

		@Override
		public void onChanged(final javafx.collections.ListChangeListener.Change<? extends RoomBean> c) {
			calculateDateToEarnings(c.getList());

		}
	}

	private static final Logger logger = LoggerFactory.getLogger(MoneyMonitor.class);

	public static MoneyMonitor getInstance() {
		return InstanceHolder.instance;
	}

	private final MapProperty<LocalDate, Number> dayToNetEarnings = new SimpleMapProperty<>(
			FXCollections.observableMap(new TreeMap<>()));

	private final MapProperty<YearMonth, Number> yearMonthToNetEarnings = new SimpleMapProperty<>(
			FXCollections.observableMap(new TreeMap<>()));

	private final DoubleProperty totalNetEarnings = new SimpleDoubleProperty();

	private MoneyMonitor() {
		RoomBeanSelectionManager.getInstance().selectionProperty().addListener(new SelectionListener());
	}

	private void calculateDateToEarnings(final ObservableList<? extends RoomBean> list) {
		final List<BookingEntry> selectedBookings = list.stream().flatMap(r -> r.getFilteredBookingEntry().toList().stream())
				.collect(Collectors.toList());

		dayToNetEarnings.clear();

		for (final BookingEntry rb : selectedBookings) {
			fillDayMap(rb);
			fillMonthYearMap(rb);
		}

		setTotalNetEarnings(dayToNetEarnings.values().stream().mapToDouble(d -> d.doubleValue()).sum());
	}

	public final MapProperty<LocalDate, Number> dayToNetEarningsProperty() {
		return this.dayToNetEarnings;
	}

	private void fillDayMap(final BookingEntry rb) {
		Number n = dayToNetEarnings.get(rb.getDate());
		if (n == null) {
			n = 0;
		}
		n = n.doubleValue() + rb.getNetEarnings();
		dayToNetEarnings.put(rb.getDate(), n);

	}

	private void fillMonthYearMap(final BookingEntry rb) {
		final YearMonth myYearMonth = YearMonth.from(rb.getDate());
		Number n = yearMonthToNetEarnings.get(myYearMonth);
		if (n == null) {
			n = 0;
		}
		n = n.doubleValue() + rb.getNetEarnings();
		yearMonthToNetEarnings.put(myYearMonth, n);

	}

	public final Map<LocalDate, Number> getDayToNetEarnings() {
		return this.dayToNetEarningsProperty().get();
	}

	public final double getTotalNetEarnings() {
		return this.totalNetEarningsProperty().get();
	}

	public final Map<YearMonth, Number> getYearMonthToNetEarnings() {
		return this.yearMonthToNetEarningsProperty().get();
	}

	private final void setDayToNetEarnings(final Map<LocalDate, Number> dateToNetEarnings) {
		this.dayToNetEarningsProperty().clear();
		this.dayToNetEarningsProperty().putAll(dateToNetEarnings);
	}

	private final void setTotalNetEarnings(final double totalNetEarnings) {
		this.totalNetEarningsProperty().set(totalNetEarnings);
	}

	private final void setYearMonthToNetEarnings(final Map<YearMonth, Number> yearMonthToNetEarnings) {
		this.yearMonthToNetEarnings.clear();
		this.yearMonthToNetEarningsProperty().putAll(yearMonthToNetEarnings);
	}

	public final DoubleProperty totalNetEarningsProperty() {
		return this.totalNetEarnings;
	}

	public final MapProperty<YearMonth, Number> yearMonthToNetEarningsProperty() {
		return this.yearMonthToNetEarnings;
	}

}

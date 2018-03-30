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

package com.github.drbookings.ui.selection;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.beans.DateBean;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class LocalDatesSelectionManager {

	private static class InstanceHolder {
		private static final LocalDatesSelectionManager instance = new LocalDatesSelectionManager();
	}

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(LocalDatesSelectionManager.class);

	public static LocalDatesSelectionManager getInstance() {
		return InstanceHolder.instance;
	}

	private static Collection<? extends LocalDate> transform(final Collection<? extends DateBean> dateBeans) {
		return dateBeans.stream().map(d -> d.getDate()).collect(Collectors.toList());
	}

	private final ListProperty<YearMonth> selectedMonths = new SimpleListProperty<>(
			FXCollections.observableArrayList());

	private final ListProperty<LocalDate> selection = new SimpleListProperty<>(FXCollections.observableArrayList());

	private LocalDatesSelectionManager() {
		DateBeanSelectionManager.getInstance().selectionProperty().addListener(new ListChangeListener<DateBean>() {

			@Override
			public void onChanged(final javafx.collections.ListChangeListener.Change<? extends DateBean> c) {
				while (c.next()) {

					selectionProperty().removeAll(transform(c.getRemoved()));
					selectionProperty().addAll(transform(c.getAddedSubList()));
					if (logger.isDebugEnabled()) {
						logger.debug("Selection updated: " + selectionProperty().size());
					}
				}
			}
		});

		selectedMonths.bind(Bindings.createObjectBinding(collectSelectedMonths(), selectionProperty()));
	}

	private Callable<ObservableList<YearMonth>> collectSelectedMonths() {
		return () -> {
			final ObservableList<YearMonth> result = FXCollections.observableArrayList(
					selectionProperty().stream().map(l -> YearMonth.from(l)).collect(Collectors.toSet()));
			return result;
		};
	}

	public final List<YearMonth> getSelectedMonths() {
		return this.selectedMonthsProperty().get();
	}

	public final List<LocalDate> getSelection() {
		return this.selectionProperty().get();
	}

	public final ListProperty<YearMonth> selectedMonthsProperty() {
		return this.selectedMonths;
	}

	public final ListProperty<LocalDate> selectionProperty() {
		return this.selection;
	}

}

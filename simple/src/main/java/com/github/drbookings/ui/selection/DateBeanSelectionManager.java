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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.DateBeans;
import com.github.drbookings.ui.beans.DateBean;
import com.google.common.collect.Range;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;

public class DateBeanSelectionManager {

    private static class InstanceHolder {
	private static final DateBeanSelectionManager instance = new DateBeanSelectionManager();
    }

    private static final Logger logger = LoggerFactory.getLogger(DateBeanSelectionManager.class);

    public static DateBeanSelectionManager getInstance() {
	return InstanceHolder.instance;
    }

    private final ListProperty<DateBean> selection = new SimpleListProperty<>(
	    FXCollections.observableArrayList(DateBean.extractor()));

    private final ObjectProperty<Range<LocalDate>> selectedDateRange = new SimpleObjectProperty<>();

    private DateBeanSelectionManager() {
	selectedDateRangeProperty().bind(Bindings.createObjectBinding(calculateDateRange(), selectionProperty()));
    }

    private Callable<Range<LocalDate>> calculateDateRange() {
	return () -> DateBeans.getDateRange(getSelection());
    }

    public final Range<LocalDate> getSelectedDateRange() {
	return this.selectedDateRangeProperty().get();
    }

    public final List<DateBean> getSelection() {
	return this.selectionProperty().get();
    }

    public final ObjectProperty<Range<LocalDate>> selectedDateRangeProperty() {
	return this.selectedDateRange;
    };

    public final ListProperty<DateBean> selectionProperty() {
	return this.selection;
    }

    public final DateBeanSelectionManager setSelection(final Collection<? extends DateBean> dates) {
	selectionProperty().setAll(dates);
	return this;
    }

}

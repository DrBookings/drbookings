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

import java.util.function.Function;

import com.github.drbookings.ui.beans.DateBean;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DateBeanBin<T> {

    private final int binSize;

    private final ObservableList<DateBean> dateBeans = FXCollections.observableArrayList();

    private final ListProperty<T> values = new SimpleListProperty<>();

    private final Function<DateBean, T> transformer;

    public DateBeanBin(final int binSize, final Function<DateBean, T> transformer) {
	this.binSize = binSize;
	this.transformer = transformer;

    }

    public void add(final DateBean dateBean) {
	if ((dateBeans.size() % binSize) == 0) {
	    for (final DateBean db : dateBeans.subList(dateBeans.size() - 1 - binSize, dateBeans.size())) {
		values.add(transformer.apply(db));
	    }
	}
	this.dateBeans.add(dateBean);
    }

    public final ObservableList<T> getValues() {
	return this.valuesProperty().get();
    }

    protected final void setValues(final ObservableList<T> values) {
	this.valuesProperty().set(values);
    }

    public final ListProperty<T> valuesProperty() {
	return this.values;
    }

}

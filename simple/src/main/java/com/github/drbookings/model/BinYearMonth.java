package com.github.drbookings.model;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

import java.time.YearMonth;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.drbookings.ui.beans.DateBean;

import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BinYearMonth {

    private class ChangeListener implements ListChangeListener<DateBean> {

	@Override
	public void onChanged(final javafx.collections.ListChangeListener.Change<? extends DateBean> c) {
	    while (c.next()) {
		if (c.wasAdded()) {
		    // addToMap(c.getAddedSubList());
		}
		if (c.wasRemoved()) {
		    // removeFromMap(c.getRemoved());
		}
		if (c.wasUpdated()) {
		    addToMap(c.getList().subList(c.getFrom(), c.getTo()));
		    // System.err.println(c.getList().subList(c.getFrom(),
		    // c.getTo()).get(0).getTotalNetEarnings());
		}
		if (c.wasReplaced()) {
		    // addToMap(c.getList().subList(c.getFrom(), c.getTo()));
		}
	    }

	}
    }

    private final ChangeListener changeListener = new ChangeListener();

    private final MapProperty<YearMonth, Collection<DateBean>> yearMonth2DateBeanMap = new SimpleMapProperty<>(
	    FXCollections.observableMap(new TreeMap<>()));

    private void addToMap(final List<? extends DateBean> addedSubList) {
	for (final DateBean db : addedSubList) {
	    final YearMonth ym = YearMonth.from(db.getDate());
	    Collection<DateBean> col = yearMonth2DateBeanMap.get(ym);
	    if (col == null) {
		col = new HashSet<>();
	    }
	    col.add(db);
	    // re-add, to trigger change event
	    yearMonth2DateBeanMap.remove(ym);
	    yearMonth2DateBeanMap.put(ym, col);

	}
    }

    public void bind(final ObservableList<? extends DateBean> newObservable) {
	newObservable.addListener(changeListener);
    }

    public final Map<YearMonth, Collection<DateBean>> getYearMonth2DateBeanMap() {
	return this.yearMonth2DateBeanMapProperty().get();
    }

    private void removeFromMap(final List<? extends DateBean> removed) {
	for (final DateBean db : removed) {
	    final YearMonth ym = YearMonth.from(db.getDate());
	    final Collection<DateBean> col = yearMonth2DateBeanMap.get(ym);
	    if (col != null) {
		col.remove(db);
	    }
	}
    }

    public final void setYearMonth2DateBeanMap(final Map<YearMonth, Collection<DateBean>> yearMonth2DateBean) {
	this.yearMonth2DateBeanMap.clear();
	this.yearMonth2DateBeanMapProperty().putAll(yearMonth2DateBean);
    }

    public void unbind(final ObservableList<? extends DateBean> newObservable) {
	newObservable.removeListener(changeListener);
    }

    public final MapProperty<YearMonth, Collection<DateBean>> yearMonth2DateBeanMapProperty() {
	return this.yearMonth2DateBeanMap;
    }

}

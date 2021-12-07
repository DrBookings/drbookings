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

import com.github.drbookings.exception.OverbookingException;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

public class SimpleUIData implements UIData {

    private static final Logger logger = LoggerFactory.getLogger(SimpleUIData.class);
    private DataStoreCore data;
    /**
     * Use provider to keep name unique.
     */
    private final CleaningFactory cleaningFactory;
    /**
     * Use provider to keep name unique.
     */
    private final RoomFactory roomFactory;
    private final UICleaningData cleaningData;
    private final ListProperty<DateBean> dates = new SimpleListProperty<>(
	    FXCollections.observableArrayList(DateBean.extractor()));
    private final MapProperty<LocalDate, DateBean> datesMap = new SimpleMapProperty<>(
	    FXCollections.observableMap(new TreeMap<>()));
    private final ListChangeListener<CleaningEntry> cleaningElementChangeListener = c -> update(c);

    public SimpleUIData() {

	this(null);
    }

    private void update(final Change<? extends CleaningEntry> c) {

	if (logger.isDebugEnabled()) {
	    logger.debug("Cleaning data change: " + c);
	}
	while (c.next()) {
	    handleAddedCleaningEntries(c.getAddedSubList());
	    handleRemovedCleaningEntries(c.getRemoved());
	}
    }

    private void handleRemovedCleaningEntries(final List<? extends CleaningEntry> removed) {
	// TODO Auto-generated method stub

    }

    private void handleAddedCleaningEntries(final List<? extends CleaningEntry> added) {

	for (final CleaningEntry ce : added) {
	    // trigger auto-fill
	    getDateBean(ce.getDate());
	}
    }

    public SimpleUIData(final DataStoreCore data) {

	cleaningFactory = CleaningFactory.getInstance();
	roomFactory = RoomFactory.getInstance();
	cleaningData = new SimpleUICleaningData(cleaningFactory, roomFactory);
	loadFrom(data);
	createBindings();
    }

    private void createBindings() {

	cleaningData.addListener(cleaningElementChangeListener);
    }

    @Override
    public final ListProperty<DateBean> datesProperty() {

	return dates;
    }

    private DateBean getDateBean(final LocalDate date) {

	DateBean result = datesMap.get(date);
	if (result == null) {
	    result = new DateBean(date);
	    result.setCleaningData(cleaningData);
	    dates.add(result);
	    datesMap.put(date, result);
	}
	return result;
    }

    @Override
    public final ObservableList<DateBean> getDates() {

	return datesProperty().get();
    }

    private void loadBookings(final Collection<? extends BookingEntry> entries) {

	for (final BookingEntry e : entries) {
	    final DateBean date = getDateBean(e.getDate());
	    final RoomBean room = date.getRoom(e.getRoom().getName());
	    final BookingEntryPair entryPair = room.getBookingEntry();
	    try {
		entryPair.addBooking(e);
	    } catch (final OverbookingException e1) {
		if (logger.isErrorEnabled()) {
		    logger.error(e1.getLocalizedMessage(), e1);
		}
	    }
	}
    }

    @Override
    public UICleaningData getCleaningData() {

	return cleaningData;
    }

    private void loadBookings() {

	for (final BookingBean b : data.getBookings()) {
	    loadBookings(Bookings.toEntries(b));
	}
    }

    private void loadCleanings() {

	for (final CleaningEntry ce : data.getCleanings()) {
	    try {
		cleaningData.add(ce);
	    } catch (final Exception e) {
		if (logger.isErrorEnabled()) {
		    logger.error("Cannot add cleaning " + ce, e);
		}
	    }
	}
    }

    private void loadExpenses() {
	// TODO Auto-generated method stub

    }

    @Override
    public final void setDates(final Collection<? extends DateBean> dates) {

	datesProperty().setAll(dates);
    }

    @Override
    public void loadFrom(final DataStoreCore data) {

	if (data != null) {
	    this.data = data;
	    loadBookings();
	    loadCleanings();
	    loadExpenses();
	}
    }
}

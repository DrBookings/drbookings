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

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BookingSelectionManager {

    private static class InstanceHolder {
	private static final BookingSelectionManager instance = new BookingSelectionManager();
    }

    private static final Logger logger = LoggerFactory.getLogger(BookingSelectionManager.class);

    public static BookingSelectionManager getInstance() {
	return InstanceHolder.instance;
    }

    private static ObservableList<BookingEntry> transform(final Collection<? extends RoomBean> rooms) {
	return rooms.stream().flatMap(r -> r.getFilteredBookingEntry().toStream())
		.collect(Collectors.toCollection(() -> FXCollections.observableArrayList(BookingEntry.extractor())));
    }

    private final ListProperty<BookingBean> bookings = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingBean.extractor()));

    private final ListProperty<BookingEntry> selection = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingEntry.extractor()));

    private BookingSelectionManager() {
	// internally update bookings (of type BookingBean)
	bookings.bind(Bindings.createObjectBinding(collectBookings(), selectionProperty()));
	// register selection listener to update selection
	RoomBeanSelectionManager.getInstance().selectionProperty().addListener((ListChangeListener<RoomBean>) c -> {
	    while (c.next()) {

	    }
	    selectionProperty().setAll(transform(c.getList()));
	});
	// initially set selection
	selectionProperty().setAll(transform(RoomBeanSelectionManager.getInstance().selectionProperty()));
    }

    public final ListProperty<BookingBean> bookingsProperty() {
	return this.bookings;
    }

    private Callable<ObservableList<BookingBean>> collectBookings() {
	return () -> {
	    final Set<BookingBean> set = selectionProperty().stream().map(e -> e.getElement())
		    .collect(Collectors.toSet());
	    final ObservableList<BookingBean> list = FXCollections.observableArrayList(BookingBean.extractor());
	    list.addAll(set);
	    return list;
	};
    }

    public final List<BookingBean> getBookings() {
	return this.bookingsProperty().get();
    }

    public final List<BookingEntry> getSelection() {
	return this.selectionProperty().get();
    }

    public final ListProperty<BookingEntry> selectionProperty() {
	return this.selection;
    }

}

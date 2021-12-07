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

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class SimpleUICleaningData extends SimpleCleaningData implements UICleaningData {

    public void addListener(ListChangeListener<? super CleaningEntry> listener) {
	entryList.addListener(listener);
    }

    public void removeListener(ListChangeListener<? super CleaningEntry> listener) {
	entryList.removeListener(listener);
    }

    /**
     * An observable copy of the elements.
     */
    private final ObservableList<CleaningEntry> entryList;

    public SimpleUICleaningData(CleaningFactory cleaningFactory, RoomFactory roomFactory) {
	super(cleaningFactory, roomFactory);
	this.entryList = FXCollections.observableArrayList(CleaningEntry.extractor());

    }

    /**
     * For testing.
     */
    public SimpleUICleaningData() {
	super();
	this.entryList = FXCollections.observableArrayList(CleaningEntry.extractor());

    }

    @Override
    public void add(final CleaningEntry ce) {
	super.add(ce);
	entryList.add(ce);
    }

    @Override
    public CleaningEntry add(LocalDate date, String name, String roomName, boolean black) {
	CleaningEntry ce = super.add(date, name, roomName, black);
	entryList.add(ce);
	return ce;
    }

}

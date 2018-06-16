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

package com.github.drbookings.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.github.drbookings.model.RoomEntry;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.DateEntry;
import com.github.drbookings.model.data.Room;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class CleaningEntry extends DateEntry<Cleaning> {

    public static enum ShortTerm {
	YES, NO, UNKNOWN
    }

    /**
     * The costs for this cleaning, i.e., the money going to the cleaning person.
     */
    private final FloatProperty cleaningCosts = new SimpleFloatProperty();

    /**
     * Bi-di relationship owned by {@code CleaningEntry}.
     */
    private final RoomEntry room;

    /**
     * The Google Calendar IDs.
     */
    private List<String> calendarIds = new ArrayList<>();

    public CleaningEntry(final RoomEntry room, final Cleaning element) {
	super(room.getDate(), element);
	this.room = room;
	/**
	 * Date is taken-over by the RoomEntry, therefore it cannot mismatch.
	 */
	this.room.setCleaning(this);
    }

    public void addCalendarId(final String id) {
	calendarIds.add(id);
    }

    public final FloatProperty cleaningCostsProperty() {
	return this.cleaningCosts;
    }

    @Override
    public boolean equals(final Object o) {
	if (this == o) {
	    return true;
	}
	if ((o == null) || (getClass() != o.getClass())) {
	    return false;
	}
	if (!super.equals(o)) {
	    return false;
	}
	final CleaningEntry that = (CleaningEntry) o;
	return Objects.equals(getRoom(), that.getRoom());
    }

    public List<String> getCalendarIds() {
	return calendarIds;
    }

    public final float getCleaningCosts() {
	return this.cleaningCostsProperty().get();
    }

    public String getName() {
	return getElement().getName();
    }

    public Room getRoom() {
	return room.getElement();
    }

    @Override
    public int hashCode() {

	return Objects.hash(super.hashCode(), getRoom());
    }

    public CleaningEntry setCalendarIds(final Collection<? extends String> calendarIds) {
	if (calendarIds != null) {
	    this.calendarIds = new ArrayList<>(calendarIds);
	}
	return this;
    }

    public final void setCleaningCosts(final float cleaningCosts) {
	this.cleaningCostsProperty().set(cleaningCosts);
    }
}

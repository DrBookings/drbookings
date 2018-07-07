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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.data.PaymentProvider;
import com.github.drbookings.model.Payment;
import com.github.drbookings.model.PaymentImpl;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.DateEntry;
import com.github.drbookings.model.data.DateEntryImpl;
import com.github.drbookings.model.data.IDed;
import com.github.drbookings.model.data.IDedImpl;
import com.github.drbookings.model.data.Room;
import com.google.common.base.Objects;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class CleaningEntry implements IDed, DateEntry<Cleaning>, PaymentProvider {

    public static enum ShortTerm {
	NO, UNKNOWN, YES
    }

    /**
     * The Google Calendar IDs.
     */
    private List<String> calendarIds = new ArrayList<>();

    /**
     * The costs for this cleaning, i.e., the money going to the cleaning person.
     */
    private final FloatProperty cleaningCosts = new SimpleFloatProperty();

    private final DateEntry<Cleaning> dateEntryDelegate;

    private final IDed idedDelegate;

    private final Room room;

    public CleaningEntry(final String id, final LocalDate date, final Room room, final Cleaning element) {
	this.room = room;
	this.dateEntryDelegate = new DateEntryImpl<>(date, element);
	this.idedDelegate = new IDedImpl(id);
    }

    public CleaningEntry(final LocalDate date, final Room room, final Cleaning element) {
	this(null, date, room, element);
    }

    public CleaningEntry(final LocalDate date, final String roomName, final String cleaningName,
	    final float cleaningCosts) {
	this(date, new Room(roomName), new Cleaning(cleaningName));
	setCleaningCosts(cleaningCosts);

    }

    public void addCalendarId(final String id) {
	calendarIds.add(id);
    }

    public final FloatProperty cleaningCostsProperty() {
	return this.cleaningCosts;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!super.equals(obj)) {
	    return false;
	}
	if (!(obj instanceof CleaningEntry)) {
	    return false;
	}
	final CleaningEntry other = (CleaningEntry) obj;

	return Objects.equal(this.getRoom(), other.getRoom()) && Objects.equal(this.getDate(), other.getDate());

    }

    public List<String> getCalendarIds() {
	return calendarIds;
    }

    public final float getCleaningCosts() {
	return this.cleaningCostsProperty().get();
    }

    @Override
    public LocalDate getDate() {
	return dateEntryDelegate.getDate();
    }

    @Override
    public Cleaning getElement() {
	return dateEntryDelegate.getElement();
    }

    @Override
    public String getId() {
	return idedDelegate.getId();
    }

    public String getName() {
	return getElement().getName();
    }

    @Override
    public List<Payment> getPayments() {
	return Arrays.asList(new PaymentImpl(getDate(), getCleaningCosts()));
    }

    public Room getRoom() {
	return room;
    }

    @Override
    public int hashCode() {
	return Objects.hashCode(getRoom(), getDate());
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

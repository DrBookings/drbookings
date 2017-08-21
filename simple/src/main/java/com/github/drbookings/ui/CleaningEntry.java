package com.github.drbookings.ui;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.data.manager.MainManager;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class CleaningEntry extends DateEntry<Cleaning> {

	private List<String> calendarIds = new ArrayList<>();

	private final FloatProperty cleaningCosts = new SimpleFloatProperty();

	/**
	 * @deprecated Move away
	 */
	@Deprecated
	private final MainManager mainManager;

	private final Booking booking;

	public Booking getBooking() {
		return booking;
	}

	public CleaningEntry(final LocalDate date, final Booking booking, final Cleaning element,
			final MainManager mainManager) {
		super(date, element);
		this.booking = booking;
		this.booking.setCleaning(this);
		this.mainManager = mainManager;
	}

	public void addCalendarId(final String id) {
		calendarIds.add(id);
	}

	public final FloatProperty cleaningCostsProperty() {
		return this.cleaningCosts;
	}

	public List<String> getCalendarIds() {
		return calendarIds;
	}

	public final float getCleaningCosts() {
		return this.cleaningCostsProperty().get();
	}

	public boolean isShortTime() {
		return mainManager.hasCheckIn(getDate(), booking.getEntry(getDate()).getRoom().getName());
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

	public Room getRoom() {
		return booking.getEntry(getDate()).getRoom();
	}

}

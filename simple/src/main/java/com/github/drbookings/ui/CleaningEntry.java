package com.github.drbookings.ui;

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

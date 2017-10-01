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

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.Cleaning;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.data.manager.MainManager;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CleaningEntry extends DateEntry<Cleaning> {

    private final FloatProperty cleaningCosts = new SimpleFloatProperty();
    /**
     * @deprecated Move away
     */
    @Deprecated
    private final MainManager mainManager;
    private final Booking booking;
    private List<String> calendarIds = new ArrayList<>();

    public CleaningEntry(final LocalDate date, final Booking booking, final Cleaning element,
                         final MainManager mainManager) {
        super(date, element);
        this.booking = booking;
        this.booking.setCleaning(this);
        this.mainManager = mainManager;
    }

    public Booking getBooking() {
        return booking;
    }

    @Override
    public String toString() {
        return "CleaningEntry{" +
                "cleaningCosts=" + getCleaningCosts() +
                ", cleaningFees=" + booking.getCleaningFees() +
                ", origin=" + booking.getBookingOrigin() +
                ", guest=" + booking.getGuest() +
                ", shortTime=" + isShortTime() +
                ", date=" + getDate() +
                ", element=" + getElement() +
                '}';
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

    public CleaningEntry setCalendarIds(final Collection<? extends String> calendarIds) {
        if (calendarIds != null) {
            this.calendarIds = new ArrayList<>(calendarIds);
        }
        return this;
    }

    public final float getCleaningCosts() {
        return this.cleaningCostsProperty().get();
    }

    public final void setCleaningCosts(final float cleaningCosts) {
        this.cleaningCostsProperty().set(cleaningCosts);
    }

    public boolean isShortTime() {
        return mainManager.hasCheckIn(getDate(), booking.getEntry(getDate()).getRoom().getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CleaningEntry)) return false;
        if (!super.equals(o)) return false;
        CleaningEntry that = (CleaningEntry) o;
        return Objects.equals(getBooking(), that.getBooking());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getBooking());
    }

    public Room getRoom() {
        return booking.getEntry(getDate()).getRoom();
    }

    public String getName() {
        return getElement().getName();
    }
}

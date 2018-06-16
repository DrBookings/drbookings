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

package com.github.drbookings.ui.beans;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.TemporalQueries;
import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.BookingEntryPair;
import com.github.drbookings.model.data.DrBookingsData;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.ui.BookingFilter;
import com.github.drbookings.ui.CleaningEntry;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

/**
 * UI Bean used by {@link DateBean} to nest room specific information.
 * <p>
 * {@code RoomBean} can be seen as a manifestation of given {@link Room} at
 * given date (via the {@link DateBean}). A Room can be empty, have one
 * (check-in or stay-over) or two (check-in, check-out) {@link BookingEntry
 * bookings}.
 * </p>
 * <p>
 * {@code RoomBean} is created-on-demand by {@link DateBean}.
 * </p>
 *
 * @author Alexander Kerner
 */
public class RoomBean extends WarnableBean {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(RoomBean.class);

    public static Callback<RoomBean, Observable[]> extractor() {
	return param -> new Observable[] { param.bookingFilterStringProperty(), param.filteredBookingEntryProperty(),
		param.cleaningEntryProperty() };
    }

    private final ObjectProperty<CleaningEntry> cleaningEntry;
    private final ObjectProperty<BookingEntryPair> bookingEntry;
    /**
     * Filtered bookings.
     */
    private final ObjectProperty<BookingEntryPair> filteredBookingEntry;

    /**
     * The room that is manifesting at this date.
     */
    private final Room room;
    /**
     * Mandatory bi-di relationship owned by {@link DateBean}.
     */
    private final DateBean dateBean;

    /**
     * the search string, on which bookings are filtered.
     */
    private final StringProperty bookingFilterString = new SimpleStringProperty();

    private final BooleanProperty needsCleaning = new SimpleBooleanProperty();

    public RoomBean(final Room room, final DateBean date) {
	this.room = Objects.requireNonNull(room);
	this.dateBean = Objects.requireNonNull(date);
	this.bookingEntry = new SimpleObjectProperty<>();
	this.cleaningEntry = new SimpleObjectProperty<>();
	this.filteredBookingEntry = new SimpleObjectProperty<>();
	bindProperties();
	loadData();

    }

    @Override
    protected void bindProperties() {
	filteredBookingEntryProperty().bind(
		Bindings.createObjectBinding(filterBookings(), bookingFilterStringProperty(), bookingEntryProperty()));
	needsCleaningProperty()
		.bind(Bindings.createObjectBinding(calulateNeedsCleaning(), getData().cleaningsChangedProperty()));
	super.bindProperties();

    }

    public ObjectProperty<BookingEntryPair> bookingEntryProperty() {
	return this.bookingEntry;
    }

    public StringProperty bookingFilterStringProperty() {
	return this.bookingFilterString;
    }

    @Override
    protected Callable<Boolean> calculateWarningProperty() {

	return () -> {

	    if (getFilteredBookingEntry() == null) {
		return false;
	    }

	    final boolean lastMonth = getDate().query(TemporalQueries::isPreviousMonthOrEarlier);

	    // if (getDate().isAfter(LocalDate.now()) && needsCleaning()) {
	    // return true;
	    // }

	    final boolean payment = getFilteredBookingEntry().toList().stream()
		    .filter(b -> b.getElement().isPaymentDone()).count() == getFilteredBookingEntry().size();

	    if (!payment && lastMonth) {
		return true;
	    }
	    final boolean welcomeMail = getFilteredBookingEntry().toList().stream()
		    .filter(b -> b.getElement().isWelcomeMailSend()).count() == getFilteredBookingEntry().size();
	    return !lastMonth && !welcomeMail;

	};
    }

    public Callable<Boolean> calulateNeedsCleaning() {
	return () -> getData().cleaningNeededFor(getName(), getDate());
    }

    public ObjectProperty<CleaningEntry> cleaningEntryProperty() {
	return this.cleaningEntry;
    }

    private Callable<BookingEntryPair> filterBookings() {

	return () -> new BookingFilter(getBookingFilterString()).test(bookingEntry.get()) ? bookingEntry.get() : null;
    }

    public ObjectProperty<BookingEntryPair> filteredBookingEntryProperty() {
	return this.filteredBookingEntry;
    }

    public BookingEntryPair getBookingEntry() {
	return this.bookingEntryProperty().get();
    }

    public String getBookingFilterString() {
	return this.bookingFilterStringProperty().get();
    }

    public CleaningEntry getCleaningEntry() {
	final CleaningEntry result = this.cleaningEntryProperty().get();
	return result;
    }

    public DrBookingsData getData() {
	return dateBean.getData();
    }

    public LocalDate getDate() {
	return dateBean.getDate();
    }

    public BookingEntryPair getFilteredBookingEntry() {
	return this.filteredBookingEntry.get();
    }

    public String getName() {
	return room.getName();
    }

    public Room getRoom() {
	return room;
    }

    @Override
    protected Observable[] getWarnableObservables() {
	return new Observable[] { filteredBookingEntryProperty(), needsCleaningProperty() };
    }

    public boolean hasCheckIn() {
	return bookingEntry.get() != null ? bookingEntry.get().hasCheckIn() : false;
    }

    public boolean hasCheckOut() {
	return bookingEntry.get() != null ? bookingEntry.get().hasCheckOut() : false;
    }

    public boolean hasCleaning() {
	return getCleaningEntry() != null;
    }

    public boolean isEmpty() {
	return filteredBookingEntry.get() == null;
    }

    private void loadBookingData() {
	final Optional<BookingEntryPair> bookingEntryPairOptional = getData().getBookingEntryPair(getName(), getDate());
	if (bookingEntryPairOptional.isPresent()) {
	    final BookingEntryPair bookingEntryPair = bookingEntryPairOptional.get();
	    bookingEntry.set(bookingEntryPair);
	}
    }

    private void loadCleaningData() {
	final Optional<CleaningEntry> cleaningEntryOptional = getData().getCleaningEntry(getName(), getDate());
	if (cleaningEntryOptional.isPresent()) {
	    final CleaningEntry cleaningEntry = cleaningEntryOptional.get();
	    setCleaningEntry(cleaningEntry);
	}
    }

    private void loadData() {
	loadBookingData();
	loadCleaningData();
	// this.needsCleaning.setValue(manager.getData().cleaningNeededFor(getName(),
	// getDate()));
    }

    public boolean needsCleaning() {
	return this.needsCleaningProperty().get();
    }

    public BooleanProperty needsCleaningProperty() {
	return this.needsCleaning;
    }

    public void setBookingFilterString(final String bookingFilterString) {
	this.bookingFilterStringProperty().set(bookingFilterString);
    }

    public void setCleaningEntry(final CleaningEntry cleaningEntry) {
	this.cleaningEntryProperty().set(cleaningEntry);
    }

    @Override
    public String toString() {
	return "roomBean:" + getDate() + ",name:" + getName() + ",filteredBooking:" + filteredBookingEntry;
    }

}

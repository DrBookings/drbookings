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

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * UI Bean used by {@link DateBean} to nest room specific information.
 * <p>
 * {@code RoomBean} can be seen as a manifestation of given {@link Room} at
 * given date (via the {@link DateBean}). A Room can be empty, have one or two
 * {@link BookingEntry booking entries}.
 * </p>
 * <p>
 * {@code RoomBean} is created-on-demand by {@link DateBean}.
 * </p>
 *
 * @author Alexander Kerner
 */
public class RoomBean extends WarnableBean implements BookingEntryPair.ChangeListener {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(RoomBean.class);

	public static Callback<RoomBean, Observable[]> extractor() {

		return param -> new Observable[]{param.filteredBookingEntryProperty(), param.cleaningEntryProperty()};
	}

	private final ObjectProperty<BookingEntryPair> bookingEntry;
	/**
	 * the search string, on which bookings are filtered.
	 */
	private final StringProperty bookingFilterString = new SimpleStringProperty();
	private final ObjectProperty<UICleaningData> cleaningData = new SimpleObjectProperty<>();
	private final ObjectProperty<UIBookingData> bookingData = new SimpleObjectProperty<>();
	private final ObjectProperty<CleaningEntry> cleaningEntry;
	/**
	 * Mandatory bi-di relationship owned by {@link DateBean}.
	 */
	private final DateBean dateBean;
	/**
	 * Filtered bookings.
	 */
	private final ObjectProperty<BookingEntryPair> filteredBookingEntry;
	private final BooleanProperty needsCleaning = new SimpleBooleanProperty();
	private final BooleanProperty needsCleaningUpdateProperty = new SimpleBooleanProperty();
	private final BooleanProperty newCleaningQueryProperty = new SimpleBooleanProperty();
	/**
	 * The room that is manifesting at this date.
	 */
	private final Room room;
	private final ListChangeListener<CleaningEntry> cleaningElementChangeListener = c -> update();
	private final ListChangeListener<BookingEntry> bookingElementChangeListener = c -> update();

	public RoomBean(final Room room, final DateBean date) {

		this.room = Objects.requireNonNull(room);
		dateBean = Objects.requireNonNull(date);
		bookingEntry = new SimpleObjectProperty<>();
		cleaningEntry = new SimpleObjectProperty<>();
		filteredBookingEntry = new SimpleObjectProperty<>();
		bindProperties();
		init(date);
	}

	private void init(final DateBean date) {

		setBookingEntry(new BookingEntryPair(date.getDate()));
	}

	@Override
	protected void bindProperties() {

		filteredBookingEntry.bind(Bindings.createObjectBinding(filterBookings(), bookingFilterString, bookingEntry));
		needsCleaning.bind(Bindings.createBooleanBinding(() -> calulateNeedsCleaning(), bookingEntry, cleaningData, needsCleaningUpdateProperty));
		bookingEntry.addListener((v, o, n) -> {
			if(o != null) {
				o.removeListener(this);
			}
			if(n != null) {
				n.addListener(this);
			}
		});
		cleaningData.addListener((v, o, n) -> {
			if(o != null) {
				o.removeListener(cleaningElementChangeListener);
			}
			if(n != null) {
				n.addListener(cleaningElementChangeListener);
			}
		});
		bookingData.addListener((v, o, n) -> {
			if(o != null) {
				o.removeListener(bookingElementChangeListener);
			}
			if(n != null) {
				n.addListener(bookingElementChangeListener);
			}
		});
		cleaningEntry.bind(Bindings.createObjectBinding(() -> queryForCleaningEntry(), cleaningData, newCleaningQueryProperty));
		bindWarningProperty();
		super.bindProperties();
	}

	public ObjectProperty<BookingEntryPair> bookingEntryProperty() {

		return bookingEntry;
	}

	public StringProperty bookingFilterStringProperty() {

		return bookingFilterString;
	}

	@Override
	protected Callable<Boolean> calculateWarningProperty() {

		return () -> {
			if(getFilteredBookingEntry() == null)
				return false;
			final boolean lastMonth = getDate().query(TemporalQueries::isPreviousMonthOrEarlier);
			// if (getDate().isAfter(LocalDate.now()) && needsCleaning()) {
			// return true;
			// }
			final boolean payment = getFilteredBookingEntry().toList().stream().filter(b -> b.getElement().isPaymentDone()).count() == getFilteredBookingEntry().size();
			if(!payment && lastMonth)
				return true;
			final boolean welcomeMail = getFilteredBookingEntry().toList().stream().filter(b -> b.getElement().isWelcomeMailSend()).count() == getFilteredBookingEntry().size();
			return !lastMonth && !welcomeMail;
		};
	}

	public boolean calulateNeedsCleaning() {

		return new CleaningNeededEvaluator(getCleaningData(), getBookingData()).evaluate(this);
	}

	public ReadOnlyObjectProperty<CleaningEntry> cleaningEntryProperty() {

		return cleaningEntry;
	}

	private Callable<BookingEntryPair> filterBookings() {

		return () -> new BookingFilter(getBookingFilterString()).test(bookingEntry.get()) ? bookingEntry.get() : null;
	}

	public ReadOnlyObjectProperty<BookingEntryPair> filteredBookingEntryProperty() {

		return filteredBookingEntry;
	}

	public BookingEntryPair getBookingEntry() {

		return bookingEntryProperty().get();
	}

	public String getBookingFilterString() {

		return bookingFilterStringProperty().get();
	}

	public CleaningEntry getCleaningEntry() {

		final CleaningEntry result = cleaningEntryProperty().get();
		return result;
	}

	@Deprecated
	public DrBookingsData getData() {

		return dateBean.getData();
	}

	public LocalDate getDate() {

		return dateBean.getDate();
	}

	public BookingEntryPair getFilteredBookingEntry() {

		return filteredBookingEntry.get();
	}

	public String getName() {

		return room.getName();
	}

	public Room getRoom() {

		return room;
	}

	@Override
	protected Observable[] getWarnableObservables() {

		return new Observable[]{filteredBookingEntryProperty(), needsCleaningProperty()};
	}

	public boolean hasCheckIn() {

		return bookingEntry.get() != null && bookingEntry.get().hasCheckIn();
	}

	public boolean hasCheckOut() {

		return bookingEntry.get() != null && bookingEntry.get().hasCheckOut();
	}

	public boolean hasCleaning() {

		return getCleaningEntry() != null;
	}

	public boolean isEmpty() {

		return filteredBookingEntry.get() == null;
	}

	private void loadBookingData() {

		final Optional<BookingEntryPair> bookingEntryPairOptional = getData().getBookingEntryPair(getName(), getDate());
		if(bookingEntryPairOptional.isPresent()) {
			final BookingEntryPair bookingEntryPair = bookingEntryPairOptional.get();
			bookingEntry.set(bookingEntryPair);
		}
	}

	private void loadCleaningData() {

		final Optional<CleaningEntry> cleaningEntryOptional = getData().getCleaningEntry(getName(), getDate());
		if(cleaningEntryOptional.isPresent()) {
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

		return needsCleaningProperty().get();
	}

	public BooleanProperty needsCleaningProperty() {

		return needsCleaning;
	}

	private CleaningEntry queryForCleaningEntry() {

		return new CleaningQuery(getCleaningData()).getFor(this);
	}

	public void setBookingEntry(final BookingEntryPair bookings) {

		bookingEntryProperty().set(bookings);
	}

	public void setBookingFilterString(final String bookingFilterString) {

		bookingFilterStringProperty().set(bookingFilterString);
	}

	void setCleaningEntry(final CleaningEntry cleaningEntry) {

		this.cleaningEntry.set(cleaningEntry);
	}

	public void setNeedsCleaning(final boolean needsCleaning) {

		this.needsCleaning.set(needsCleaning);
	}

	@Override
	public String toString() {

		return "roomBean:" + getDate() + ",name:" + getName() + ",filteredBooking:" + filteredBookingEntry;
	}

	@Override
	public void update() {

		// trigger a refresh of needsCleaning and cleaningEntry
		newCleaningQueryProperty.set(!newCleaningQueryProperty.get());
		needsCleaningUpdateProperty.set(!needsCleaningUpdateProperty.get());
	}

	public final ObjectProperty<UICleaningData> cleaningDataProperty() {

		return cleaningData;
	}

	public final UICleaningData getCleaningData() {

		return cleaningDataProperty().get();
	}

	public final void setCleaningData(final UICleaningData cleaningData) {

		cleaningDataProperty().set(cleaningData);
	}

	public final ObjectProperty<UIBookingData> bookingDataProperty() {

		return bookingData;
	}

	public final UIBookingData getBookingData() {

		return bookingDataProperty().get();
	}

	public final void setBookingData(final UIBookingData bookingData) {

		bookingDataProperty().set(bookingData);
	}
}

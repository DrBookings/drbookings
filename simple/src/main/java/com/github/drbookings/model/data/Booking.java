package com.github.drbookings.model.data;

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

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.Scripting;
import com.github.drbookings.TemporalQueries;
import com.github.drbookings.model.DefaultNetEarningsCalculator;
import com.github.drbookings.model.EarningsProvider;
import com.github.drbookings.model.GrossEarningsProvider;
import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.NetEarningsCalculator;
import com.github.drbookings.model.NetEarningsProvider;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.CleaningEntry;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class Booking extends IDed
		implements Comparable<Booking>, NetEarningsProvider, GrossEarningsProvider, EarningsProvider, IBooking {

	public static Callback<Booking, Observable[]> extractor() {
		return param -> new Observable[] { param.serviceFeeProperty(), param.serviceFeesPercentProperty(),
				param.grossEarningsProperty(), param.cleaningProperty(), param.netEarningsProperty(),
				param.checkInNoteProperty(), param.checkOutNoteProperty(), param.specialRequestNoteProperty(),
				param.paymentDoneProperty(), param.welcomeMailSendProperty(), param.dateOfPaymentProperty() };
	}

	public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(Booking.class);

	private final RoundingMode roundingMode = DEFAULT_ROUNDING_MODE;

	private String externalId;

	private final LocalDate checkIn;

	private final LocalDate checkOut;

	private final Guest guest;

	private final Room room;

	private final BookingOrigin bookingOrigin;

	private final FloatProperty serviceFees = new SimpleFloatProperty();

	private final FloatProperty serviceFeesPercent = new SimpleFloatProperty();

	private final FloatProperty grossEarnings = new SimpleFloatProperty();

	private final FloatProperty cleaningFees = new SimpleFloatProperty();

	private final FloatProperty netEarnings = new SimpleFloatProperty();

	private final StringProperty grossEarningsExpression = new SimpleStringProperty();

	private final StringProperty checkInNote = new SimpleStringProperty();

	private final StringProperty checkOutNote = new SimpleStringProperty();

	private final StringProperty specialRequestNote = new SimpleStringProperty();

	private final BooleanProperty welcomeMailSend = new SimpleBooleanProperty(false);

	private final BooleanProperty paymentDone = new SimpleBooleanProperty(false);

	private final BooleanProperty splitBooking = new SimpleBooleanProperty(false);

	private final ObjectProperty<LocalDate> dateOfPayment = new SimpleObjectProperty<>(null);

	/**
	 * Set by the cleaning entry.
	 */
	private final ObjectProperty<CleaningEntry> cleaning = new SimpleObjectProperty<>();

	private List<String> calendarIds = new ArrayList<>();

	public Booking(final Guest guest, final Room room, final BookingOrigin origin, final LocalDate checkIn,
			final LocalDate checkOut) {
		this(null, guest, room, origin, checkIn, checkOut);

	}

	public Booking(final String id, final Guest guest, final Room room, final BookingOrigin origin,
			final LocalDate checkIn, final LocalDate checkOut) {
		super(id);
		Objects.requireNonNull(guest);
		Objects.requireNonNull(room);
		Objects.requireNonNull(origin);
		Objects.requireNonNull(checkIn);
		Objects.requireNonNull(checkOut);

		this.checkIn = checkIn;
		this.checkOut = checkOut;
		this.guest = guest;
		this.room = room;
		this.bookingOrigin = origin;

		bindProperties();

	}

	public void addCalendarId(final String id) {
		if (id != null) {
			calendarIds.add(id);
		}

	}

	private void bindProperties() {
		// if (logger.isDebugEnabled()) {
		// logger.debug("Binding on " + Thread.currentThread().getName());
		// }
		grossEarningsProperty()
				.bind(Bindings.createObjectBinding(evaluateExpression(), grossEarningsExpressionProperty()));
		netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), grossEarningsProperty(),
				cleaningFeesProperty(), serviceFeeProperty(), serviceFeesPercentProperty(), cleaningProperty(),
				SettingsManager.getInstance().showNetEarningsProperty()));
		paymentDoneProperty().addListener((c, o, n) -> {
			if (n && getDateOfPayment() == null) {
				setDateOfPayment(LocalDate.now());
			} else if (!n) {
				setDateOfPayment(null);
			}
		});
		dateOfPaymentProperty().addListener((c, o, n) -> {
			if (n != null) {
				setPaymentDone(true);
			} else {
				setPaymentDone(false);
			}
		});
	}

	private Callable<Number> calculateNetEarnings() {

		return () -> {
			final NetEarningsCalculator c = new DefaultNetEarningsCalculator();
			final Number result = c.apply(this);
			return result;
		};
	}

	public StringProperty checkInNoteProperty() {
		return this.checkInNote;
	}

	public StringProperty checkOutNoteProperty() {
		return this.checkOutNote;
	}

	@Override
	public int compareTo(final Booking o) {
		return getCheckIn().compareTo(o.getCheckIn());
	}

	private Callable<Number> evaluateExpression() {
		return () -> {
			return Scripting.evaluateExpression(getGrossEarningsExpression());
		};
	}

	@Override
	public BookingOrigin getBookingOrigin() {
		return bookingOrigin;
	}

	public List<String> getCalendarIds() {
		return calendarIds;
	}

	public LocalDate getCheckIn() {
		return checkIn;
	}

	public String getCheckInNote() {
		return this.checkInNoteProperty().get();
	}

	public LocalDate getCheckOut() {
		return checkOut;
	}

	public String getCheckOutNote() {
		return this.checkOutNoteProperty().get();
	}

	@Override
	public float getEarnings(final boolean netEarnings) {
		if (netEarnings) {
			return getNetEarnings();
		}
		return getGrossEarnings();
	}

	public String getExternalId() {
		return externalId;
	}

	@Override
	public float getGrossEarnings() {
		return this.grossEarningsProperty().get();
	}

	public String getGrossEarningsExpression() {
		return this.grossEarningsExpressionProperty().get();
	}

	public Guest getGuest() {
		return guest;
	}

	@Override
	public float getNetEarnings() {
		return this.netEarningsProperty().get();
	}

	public long getNumberOfDays() {
		final long daysElapsed = ChronoUnit.DAYS.between(getCheckIn(), getCheckOut());
		return daysElapsed + 1;
	}

	public long getNumberOfNights() {
		final long daysElapsed = getNumberOfDays();
		return daysElapsed - 1;
	}

	public Room getRoom() {
		return room;
	}

	public RoundingMode getRoundingMode() {
		return roundingMode;
	}

	public float getServiceFee() {
		return this.serviceFeeProperty().get();
	}

	public String getSpecialRequestNote() {
		return this.specialRequestNoteProperty().get();
	}

	public StringProperty grossEarningsExpressionProperty() {
		return this.grossEarningsExpression;
	}

	@Override
	public FloatProperty grossEarningsProperty() {
		return this.grossEarnings;
	}

	@Override
	public boolean isPaymentDone() {
		return this.paymentDoneProperty().get();
	}

	public boolean isWelcomeMailSend() {
		return this.welcomeMailSendProperty().get();
	}

	@Override
	public FloatProperty netEarningsProperty() {
		return this.netEarnings;
	}

	public BooleanProperty paymentDoneProperty() {
		return this.paymentDone;
	}

	public FloatProperty serviceFeeProperty() {
		return this.serviceFees;
	}

	public void setCalendarIds(final Collection<? extends String> calendarIds) {
		if (calendarIds != null) {
			this.calendarIds = new ArrayList<>(calendarIds);
		}
	}

	public void setCheckInNote(final String checkInNote) {
		this.checkInNoteProperty().set(checkInNote);
	}

	public void setCheckOutNote(final String checkOutNote) {
		this.checkOutNoteProperty().set(checkOutNote);
	}

	public void setExternalId(final String externalId) {
		this.externalId = externalId;
	}

	public void setGrossEarnings(final float grossEarnings) {
		this.grossEarningsProperty().set(grossEarnings);
		// if (logger.isDebugEnabled()) {
		// logger.debug("Gross Earnings changed to " + getGrossEarnings());
		// }

	}

	public void setGrossEarningsExpression(final String expression) {
		// System.err.println("set " + expression);
		this.grossEarningsExpressionProperty().set(expression);
	}

	public void setNetEarnings(final float netEarnings) {
		this.netEarningsProperty().set(netEarnings);
	}

	public void setPaymentDone(final boolean paymentDone) {
		this.paymentDoneProperty().set(paymentDone);
	}

	public void setServiceFee(final float serviceFee) {
		this.serviceFeeProperty().set(serviceFee);
	}

	public void setSpecialRequestNote(final String specialRequestNote) {
		this.specialRequestNoteProperty().set(specialRequestNote);
	}

	public void setWelcomeMailSend(final boolean welcomeMailSend) {
		this.welcomeMailSendProperty().set(welcomeMailSend);
	}

	public StringProperty specialRequestNoteProperty() {
		return this.specialRequestNote;
	}

	@Override
	public String toString() {
		return "Booking{" +
				"checkIn=" + checkIn +
				",\tcheckOut=" + checkOut +
				",\tguest=" + guest +
				",\troom=" + room +
				",\tbookingOrigin=" + bookingOrigin +
				",\tcleaningFees=" + cleaningFees +
				",\tcleaning=" + cleaning +
				'}';
	}

	public BooleanProperty welcomeMailSendProperty() {
		return this.welcomeMailSend;
	}

	public final FloatProperty cleaningFeesProperty() {
		return this.cleaningFees;
	}

	public final float getCleaningFees() {
		return this.cleaningFeesProperty().get();
	}

	public final void setCleaningFees(final float cleaningFees) {
		this.cleaningFeesProperty().set(cleaningFees);
	}

	public final FloatProperty serviceFeesPercentProperty() {
		return this.serviceFeesPercent;
	}

	public final float getServiceFeesPercent() {
		return this.serviceFeesPercentProperty().get();
	}

	public final void setServiceFeesPercent(final float serviceFeesPercent) {
		this.serviceFeesPercentProperty().set(serviceFeesPercent);
	}

	public BookingEntry getEntry(final LocalDate date) {
		if (date.isBefore(getCheckIn()) && date.isAfter(getCheckOut())) {
			throw new NoSuchElementException(
					"For date " + date + "checkin:" + getCheckIn() + ",checkout:" + getCheckOut());
		}
		return new BookingEntry(date, this);
	}

	/**
	 * Set by the cleaning entry.
	 *
	 * @return
	 */
	public final ObjectProperty<CleaningEntry> cleaningProperty() {
		return this.cleaning;
	}

	public final CleaningEntry getCleaning() {
		return this.cleaningProperty().get();
	}

	/**
	 * Set by the cleaning entry.
	 *
	 * @param cleaning
	 */
	public final void setCleaning(final CleaningEntry cleaning) {
		this.cleaningProperty().set(cleaning);
	}

	public final ObjectProperty<LocalDate> dateOfPaymentProperty() {
		return this.dateOfPayment;
	}

	public final LocalDate getDateOfPayment() {
		return this.dateOfPaymentProperty().get();
	}

	public final void setDateOfPayment(final LocalDate dateOfPayment) {
		this.dateOfPaymentProperty().set(dateOfPayment);
	}

	public final BooleanProperty splitBookingProperty() {
		return this.splitBooking;
	}

	public final boolean isSplitBooking() {
		return this.splitBookingProperty().get();
	}

	public final void setSplitBooking(final boolean splitBooking) {
		this.splitBookingProperty().set(splitBooking);
	}

	@Override
	public boolean isPaymentOverdue() {
		final boolean lastMonth = getCheckIn().query(TemporalQueries::isPreviousMonthOrEarlier);

		if (!isPaymentDone() && lastMonth) {
			return true;
		}
		return false;
	}

}

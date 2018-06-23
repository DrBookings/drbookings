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

package com.github.drbookings.model.data;

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
import com.github.drbookings.data.numbers.earnings.DefaultNetEarningsCalculator;
import com.github.drbookings.data.numbers.earnings.NetEarningsCalculator;
import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.EarningsProvider;
import com.github.drbookings.model.GrossEarningsProvider;
import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.NetEarningsProvider;
import com.github.drbookings.model.Payment;
import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

/**
 * A booking.<br>
 * That is, a certain period of time ({@link #getCheckIn()} ->
 * {@link #getCheckOut()}), for which a certain guest ({@link Guest}) has booked
 * a certain {@link Room}.<br>
 * A {@code BookingBean} can be split into multiple {@link BookingEntry booking
 * entries}.<br>
 * {@code BookingBean} is used both in UI and Model.<br>
 *
 * @see BookingEntry
 * @ee Guest
 * @see Room
 */
public class BookingBean extends IDed
	implements Comparable<BookingBean>, NetEarningsProvider, GrossEarningsProvider, EarningsProvider, IBooking {

    // TODO: rename type to avoid confusions about Model/ UI

    /**
     *
     */
    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final Logger logger = LoggerFactory.getLogger(BookingBean.class);

    public static Callback<BookingBean, Observable[]> extractor() {
	return param -> new Observable[] { param.serviceFeeProperty(), param.serviceFeesPercentProperty(),
		param.grossEarningsProperty(), param.netEarningsProperty(), param.checkInNoteProperty(),
		param.checkOutNoteProperty(), param.specialRequestNoteProperty(), param.paymentDoneProperty(),
		param.welcomeMailSendProperty(), param.dateOfPaymentProperty() };
    }

    private final RoundingMode roundingMode = DEFAULT_ROUNDING_MODE;
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
    private final DoubleProperty paymentSoFar = new SimpleDoubleProperty();
    /**
     * A (manual) flag to indicate that this booking is part of a previous one.
     */
    private final BooleanProperty splitBooking = new SimpleBooleanProperty(false);
    @Deprecated
    private final ObjectProperty<LocalDate> dateOfPayment = new SimpleObjectProperty<>(null);
    private final ListProperty<Payment> payments = new SimpleListProperty<>(FXCollections.observableArrayList());
    private String externalId;

    private List<String> calendarIds = new ArrayList<>();

    public BookingBean(final Guest guest, final Room room, final BookingOrigin origin, final LocalDate checkIn,
	    final LocalDate checkOut) {
	this(null, guest, room, origin, checkIn, checkOut);

    }

    public BookingBean(final String id, final Guest guest, final Room room, final BookingOrigin origin,
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
	bookingOrigin = origin;

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
	grossEarnings.bind(Bindings.createObjectBinding(evaluateExpression(), grossEarningsExpressionProperty()));
	netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), grossEarningsProperty(),
		cleaningFeesProperty(), serviceFeeProperty(), serviceFeesPercentProperty(),
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
	paymentSoFar.bind(Bindings.createObjectBinding(calculatePaymentSoFar(), payments));
	paymentSoFar.addListener((o, ov, nv) -> {
	    if (nv != null && nv.floatValue() > 0) {
		setPaymentDone(true);
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

    public Callable<Number> calculatePaymentSoFar() {
	return () -> payments.stream().mapToDouble(p -> p.getAmount().getNumber().doubleValue()).sum();
    }

    public StringProperty checkInNoteProperty() {
	return checkInNote;
    }

    public StringProperty checkOutNoteProperty() {
	return checkOutNote;
    }

    public final FloatProperty cleaningFeesProperty() {
	return cleaningFees;
    }

    @Override
    public int compareTo(final BookingBean o) {
	return getCheckIn().compareTo(o.getCheckIn());
    }

    public final ObjectProperty<LocalDate> dateOfPaymentProperty() {
	return dateOfPayment;
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
	return checkInNoteProperty().get();
    }

    public LocalDate getCheckOut() {
	return checkOut;
    }

    public String getCheckOutNote() {
	return checkOutNoteProperty().get();
    }

    public final float getCleaningFees() {
	return cleaningFeesProperty().get();
    }

    /**
     * @deprecated there can be more than one payment date
     * @return
     */
    @Deprecated
    public final LocalDate getDateOfPayment() {
	return dateOfPaymentProperty().get();
    }

    @Override
    public float getEarnings(final boolean netEarnings) {
	if (netEarnings) {
	    return getNetEarnings();
	}
	return getGrossEarnings();
    }

    public List<BookingEntry> getEntries() {
	return Bookings.toEntries(this);
    }

    public BookingEntry getEntry(final LocalDate date) {
	if (date.isBefore(getCheckIn()) && date.isAfter(getCheckOut())) {
	    throw new NoSuchElementException(
		    "For date " + date + "checkin:" + getCheckIn() + ",checkout:" + getCheckOut());
	}
	return new BookingEntry(date, this);
    }

    public String getExternalId() {
	return externalId;
    }

    @Override
    public float getGrossEarnings() {
	return grossEarningsProperty().get();
    }

    public String getGrossEarningsExpression() {
	return grossEarningsExpressionProperty().get();
    }

    public Guest getGuest() {
	return guest;
    }

    @Override
    public float getNetEarnings() {
	return netEarningsProperty().get();
    }

    public int getNumberOfDays() {
	final int daysElapsed = (int) ChronoUnit.DAYS.between(getCheckIn(), getCheckOut());
	return daysElapsed + 1;
    }

    public int getNumberOfNights() {
	final int daysElapsed = getNumberOfDays();
	return daysElapsed - 1;
    }

    public final List<Payment> getPayments() {
	return payments.get();
    }

    public final double getPaymentSoFar() {
	return paymentSoFar.get();
    }

    public Room getRoom() {
	return room;
    }

    public RoundingMode getRoundingMode() {
	return roundingMode;
    }

    public float getServiceFee() {
	return serviceFeeProperty().get();
    }

    public final float getServiceFeesPercent() {
	return serviceFeesPercentProperty().get();
    }

    public String getSpecialRequestNote() {
	return specialRequestNoteProperty().get();
    }

    public StringProperty grossEarningsExpressionProperty() {
	return grossEarningsExpression;
    }

    @Override
    public ReadOnlyFloatProperty grossEarningsProperty() {
	return grossEarnings;
    }

    @Override
    public boolean isPaymentDone() {
	return paymentDoneProperty().get();
    }

    public final boolean isSplitBooking() {
	return splitBookingProperty().get();
    }

    public boolean isWelcomeMailSend() {
	return welcomeMailSendProperty().get();
    }

    @Override
    public FloatProperty netEarningsProperty() {
	return netEarnings;
    }

    public BooleanProperty paymentDoneProperty() {
	return paymentDone;
    }

    public ReadOnlyDoubleProperty paymentSoFarProperty() {
	return paymentSoFar;
    }

    public ListProperty<Payment> paymentsProperty() {
	return payments;
    }

    public FloatProperty serviceFeeProperty() {
	return serviceFees;
    }

    public final FloatProperty serviceFeesPercentProperty() {
	return serviceFeesPercent;
    }

    public void setCalendarIds(final Collection<? extends String> calendarIds) {
	if (calendarIds != null) {
	    this.calendarIds = new ArrayList<>(calendarIds);
	}
    }

    public void setCheckInNote(final String checkInNote) {
	checkInNoteProperty().set(checkInNote);
    }

    public void setCheckOutNote(final String checkOutNote) {
	checkOutNoteProperty().set(checkOutNote);
    }

    public final void setCleaningFees(final float cleaningFees) {
	cleaningFeesProperty().set(cleaningFees);
    }

    public final void setDateOfPayment(final LocalDate dateOfPayment) {
	dateOfPaymentProperty().set(dateOfPayment);
    }

    public void setExternalId(final String externalId) {
	this.externalId = externalId;
    }

    public void setGrossEarningsExpression(final String expression) {
	// System.err.println("set " + expression);
	grossEarningsExpressionProperty().set(expression);
    }

    public void setNetEarnings(final float netEarnings) {
	netEarningsProperty().set(netEarnings);
    }

    public void setPaymentDone(final boolean paymentDone) {
	paymentDoneProperty().set(paymentDone);
    }

    public final void setPayments(final Collection<? extends Payment> payments) {
	this.payments.setAll(payments);
    }

    public void setServiceFee(final float serviceFee) {
	serviceFeeProperty().set(serviceFee);
    }

    public final void setServiceFeesPercent(final float serviceFeesPercent) {
	serviceFeesPercentProperty().set(serviceFeesPercent);
    }

    public void setSpecialRequestNote(final String specialRequestNote) {
	specialRequestNoteProperty().set(specialRequestNote);
    }

    public final void setSplitBooking(final boolean splitBooking) {
	splitBookingProperty().set(splitBooking);
    }

    public void setWelcomeMailSend(final boolean welcomeMailSend) {
	welcomeMailSendProperty().set(welcomeMailSend);
    }

    public StringProperty specialRequestNoteProperty() {
	return specialRequestNote;
    }

    public final BooleanProperty splitBookingProperty() {
	return splitBooking;
    }

    public String toTSV() {
	return getFormattedOriginString() + getTSVSeparator() + getCheckIn() + getTSVSeparator() + getCheckOut()
		+ getTSVSeparator() + getFormattedNumberOfNightsString();
    }

    public static final String DEFAULT_TSV_SEPARATOR = "\t";

    public String getTSVSeparator() {
	return DEFAULT_TSV_SEPARATOR;
    }

    private String getFormattedOriginString() {
	return String.format("%7s", getBookingOrigin());
    }

    public String getFormattedNumberOfNightsString() {
	return String.format("%3d", getNumberOfNights());
    }

    @Override
    public String toString() {
	return "BookingBean{ " + getFormattedOriginString() + ": " + getCheckIn() + " -> " + getCheckOut() + ", nights:"
		+ getFormattedNumberOfNightsString() + ",\tguest=" + guest + ",\troom=" + room + ",\tcleaningFees="
		+ cleaningFees + '}';
    }

    public BooleanProperty welcomeMailSendProperty() {
	return welcomeMailSend;
    }
}

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

import com.google.common.collect.Range;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

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
public class BookingBean extends IDedImpl implements Comparable<BookingBean>, NetEarningsProvider,
	GrossEarningsProvider, EarningsProvider, IBooking, PaymentProvider, PaymentsProviderBean {

    // TODO: rename type to avoid confusions about Model/ UI
    @Deprecated
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
	    if (n && (getDateOfPayment() == null)) {
		setDateOfPayment(LocalDate.now());
	    } else if (!n) {
		setDateOfPayment(null);
	    }
	});
	dateOfPaymentProperty().addListener((c, o, n) -> {
        setPaymentDone(n != null);
	});
	paymentSoFar.bind(Bindings.createObjectBinding(calculatePaymentSoFar(), payments));
	paymentSoFar.addListener((o, ov, nv) -> {
	    if ((nv != null) && (nv.floatValue() > 0)) {
		setPaymentDone(true);
	    }
	});
    }

    private Callable<Number> calculateNetEarnings() {

	return () -> {
	    final NetIncomeSupplier c = new DefaultNetIncomeSupplier();
	    final MonetaryAmount result = c.apply(this);
	    return result.getNumber();
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
	    final Number n = Scripting.evaluateExpression(getGrossEarningsExpression());
	    // System.err.println(getGrossEarningsExpression());
	    // System.err.println(n.floatValue());
	    // System.err.println();
	    return n;
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

	if (netEarnings)
	    return getNetEarnings();
	return getGrossEarnings();
    }

    public List<BookingEntry> getEntries() {

	return Bookings.toEntries(this);
    }

    public BookingEntry getEntry(final LocalDate date) {

	if (date.isBefore(getCheckIn()) && date.isAfter(getCheckOut()))
	    throw new NoSuchElementException(
		    "For date " + date + "checkin:" + getCheckIn() + ",checkout:" + getCheckOut());
	return new BookingEntry(date, this);
    }

    public String getExternalId() {

	return externalId;
    }

    /**
     * @deprecated Use getPayments() instead.
     */
    @Deprecated
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

    /**
     * @deprecated Use getPayments() instead.
     */
    @Deprecated
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

    @Override
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

    /**
     * @deprecated Use getPayments() and {@link #getServiceFeesPercent()} instead.
     * @return the total amount of service fees
     */
    @Deprecated
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

    @Override
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

    public String getFormattedOriginString() {

	return String.format("%7s", getBookingOrigin());
    }

    public String getFormattedNumberOfNightsString() {

	return String.format("%3d", getNumberOfNights());
    }

    @Override
    public String toString() {

	return "BookingBean " + getFormattedOriginString() + ": " + getCheckIn() + " -> " + getCheckOut() + ", nights:"
		+ getFormattedNumberOfNightsString() + ", room=" + room + ", " + getGuest().getName();
    }

    public BooleanProperty welcomeMailSendProperty() {

	return welcomeMailSend;
    }

    public Range<LocalDate> getDateRange() {

	return Range.closed(getCheckIn(), getCheckOut());
    }

    @Override
    public StringProperty getExpectedExpression() {
	return grossEarningsExpressionProperty();
    }

    @Override
    public void setExpectedExpression(final String expression) {
	setGrossEarningsExpression(expression);

    }

}

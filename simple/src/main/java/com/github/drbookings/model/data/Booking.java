package com.github.drbookings.model.data;

import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.DefaultNetEarningsCalculator;
import com.github.drbookings.model.NetEarningsCalculator;
import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Booking extends IDed implements Comparable<Booking> {

    public static final RoundingMode DEFAULT_ROUNDING_MODE = RoundingMode.HALF_UP;

    private static final Logger logger = LoggerFactory.getLogger(Booking.class);

    private final RoundingMode roundingMode = DEFAULT_ROUNDING_MODE;

    private String externalId;

    private final LocalDate checkIn;

    private final LocalDate checkOut;

    private final Guest guest;

    private final Room room;

    private final BookingOrigin bookingOrigin;

    private final DoubleProperty serviceFee = new SimpleDoubleProperty(Float.valueOf(0));

    private final DoubleProperty grossEarnings = new SimpleDoubleProperty();

    private final DoubleProperty netEarnings = new SimpleDoubleProperty();

    private final StringProperty grossEarningsExpression = new SimpleStringProperty();

    private final StringProperty checkInNote = new SimpleStringProperty();

    private final StringProperty checkOutNote = new SimpleStringProperty();

    private final StringProperty specialRequestNote = new SimpleStringProperty();

    private final BooleanProperty welcomeMailSend = new SimpleBooleanProperty(false);

    private final BooleanProperty paymentDone = new SimpleBooleanProperty(false);

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

    private void bindProperties() {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Binding on " + Thread.currentThread().getName());
	// }
	grossEarningsProperty()
		.bind(Bindings.createObjectBinding(evaluateExpression(), grossEarningsExpressionProperty()));
	netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), grossEarningsProperty(),
		SettingsManager.getInstance().cleaningFeesProperty()));
    }

    private Callable<Number> calculateNetEarnings() {

	return () -> {
	    final NetEarningsCalculator c = new DefaultNetEarningsCalculator();
	    if (getGuest().getName().contains("Lien")) {
		final int wait = 0;
	    }
	    c.setFees(SettingsManager.getInstance().getCleaningFees());
	    final float result = c.calculateNetEarnings((float) getGrossEarnings(), getBookingOrigin().getName());
	    return result;
	};
    }

    public StringProperty checkInNoteProperty() {
	return this.checkInNote;
    }

    private Callable<Number> evaluateExpression() {
	return () -> {
	    final String expression = getGrossEarningsExpression();
	    if (expression == null || expression.trim().length() < 1) {
		return 0;
	    }
	    final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
	    final Object result = engine.eval(expression);
	    // if (logger.isDebugEnabled()) {
	    // logger.debug("Expression result: " + result);
	    // }
	    if (result instanceof Number) {
		return (Number) result;
	    }
	    return -1;
	};
    }

    public BookingOrigin getBookingOrigin() {
	return bookingOrigin;
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

    public String getExternalId() {
	return externalId;
    }

    public double getGrossEarnings() {
	return this.grossEarningsProperty().get();
    }

    public String getGrossEarningsExpression() {
	return this.grossEarningsExpressionProperty().get();
    }

    public Guest getGuest() {
	return guest;
    }

    public double getNetEarnings() {
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

    public double getServiceFee() {
	return this.serviceFeeProperty().get();
    }

    public StringProperty grossEarningsExpressionProperty() {
	return this.grossEarningsExpression;
    }

    public DoubleProperty grossEarningsProperty() {
	return this.grossEarnings;
    }

    public boolean isPaymentDone() {
	return this.paymentDoneProperty().get();
    }

    public boolean isWelcomeMailSend() {
	return this.welcomeMailSendProperty().get();
    }

    public DoubleProperty netEarningsProperty() {
	return this.netEarnings;
    }

    public BooleanProperty paymentDoneProperty() {
	return this.paymentDone;
    }

    public DoubleProperty serviceFeeProperty() {
	return this.serviceFee;
    }

    public void setCheckInNote(final String checkInNote) {
	this.checkInNoteProperty().set(checkInNote);
    }

    public void setExternalId(final String externalId) {
	this.externalId = externalId;
    }

    public void setGrossEarnings(final double grossEarnings) {
	this.grossEarningsProperty().set(grossEarnings);
	// if (logger.isDebugEnabled()) {
	// logger.debug("Gross Earnings changed to " + getGrossEarnings());
	// }

    }

    public void setGrossEarningsExpression(final String expression) {
	// System.err.println("set " + expression);
	this.grossEarningsExpressionProperty().set(expression);
    }

    public void setNetEarnings(final double netEarnings) {
	this.netEarningsProperty().set(netEarnings);
    }

    public void setPaymentDone(final boolean paymentDone) {
	this.paymentDoneProperty().set(paymentDone);
    }

    public void setServiceFee(final double serviceFee) {
	this.serviceFeeProperty().set(serviceFee);
    }

    public void setWelcomeMailSend(final boolean welcomeMailSend) {
	this.welcomeMailSendProperty().set(welcomeMailSend);
    }

    @Override
    public String toString() {
	return "room:" + getRoom() + ",guest:" + getGuest() + ",checkIn:" + getCheckIn() + ",checkOut:" + getCheckOut()
		+ ",earnings:" + getGrossEarnings();
    }

    public BooleanProperty welcomeMailSendProperty() {
	return this.welcomeMailSend;
    }

    public StringProperty specialRequestNoteProperty() {
	return this.specialRequestNote;
    }

    public String getSpecialRequestNote() {
	return this.specialRequestNoteProperty().get();
    }

    public void setSpecialRequestNote(final String specialRequestNote) {
	this.specialRequestNoteProperty().set(specialRequestNote);
    }

    public StringProperty checkOutNoteProperty() {
	return this.checkOutNote;
    }

    public String getCheckOutNote() {
	return this.checkOutNoteProperty().get();
    }

    public void setCheckOutNote(final String checkOutNote) {
	this.checkOutNoteProperty().set(checkOutNote);
    }

    @Override
    public int compareTo(final Booking o) {
	return getCheckIn().compareTo(o.getCheckIn());
    }

}

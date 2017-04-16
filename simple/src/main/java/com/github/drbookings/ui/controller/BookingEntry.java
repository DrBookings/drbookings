package com.github.drbookings.ui.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.github.drbookings.model.DefaultNetEarningsCalculator;
import com.github.drbookings.model.NetEarningsCalculator;
import com.github.drbookings.model.data.Booking;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class BookingEntry extends DateRoomEntry<Booking> {

    public static Callback<BookingEntry, Observable[]> extractor() {
	return param -> new Observable[] { param.netEarningsProperty(), param.getElement().paymentDoneProperty(),
		param.getElement().welcomeMailSendProperty() };
    }

    /**
     * Gross earnings for this particular day.
     */
    private final DoubleProperty grossEarnings = new SimpleDoubleProperty();

    /**
     * Gross earnings string for this particular day.
     */
    private final StringProperty grossEarningsString = new SimpleStringProperty();

    @Override
    public String toString() {
	return "date:" + getDate() + ",room:" + getRoom() + ",guest:" + getElement().getGuest();
    }

    /**
     * Net earnings for this particular day.
     */
    private final DoubleProperty netEarnings = new SimpleDoubleProperty();

    private static final NetEarningsCalculator netEarningsCalculator = new DefaultNetEarningsCalculator();

    public BookingEntry(final LocalDate date, final Booking booking) {
	super(date, booking.getRoom(), booking);
	grossEarningsProperty()
		.bind(Bindings.createObjectBinding(calculateGrossEarnings(), getElement().grossEarningsProperty()));
	netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), grossEarningsProperty()));
    }

    private Callable<Number> calculateNetEarnings() {

	return () -> {
	    // TODO hard-coded check-out
	    if (isCheckOut()) {
		return 0;
	    }
	    return netEarningsCalculator.calculateNetEarnings((float) getGrossEarnings(),
		    getElement().getBookingOrigin().getName());
	};
    }

    private Callable<Number> calculateGrossEarnings() {
	return () -> {
	    // TODO hard-coded check-out
	    if (isCheckOut()) {
		return 0;
	    }
	    // System.err.println(getElement().getGrossEarnings());
	    // System.err.println(getElement().getNumberOfDays());
	    final double result = getElement().getGrossEarnings() / getElement().getNumberOfDays();
	    // System.err.println(result);
	    return result;
	};
    }

    public boolean isCheckIn() {
	return getDate().equals(getElement().getCheckIn());
    }

    public boolean isCheckOut() {
	return getDate().equals(getElement().getCheckOut());
    }

    public static Set<String> guestNameView(final Collection<? extends BookingEntry> bookings) {
	return bookings.stream().map(b -> b.getElement().getGuest().getName())
		.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static List<BookingEntry> checkInView(final Collection<? extends BookingEntry> bookings) {
	return bookings.stream().filter(b -> b.isCheckIn()).collect(Collectors.toList());
    }

    public static List<BookingEntry> checkOutView(final Collection<? extends BookingEntry> bookings) {
	return bookings.stream().filter(b -> b.isCheckOut()).collect(Collectors.toList());
    }

    public static List<Booking> getBookings(final Collection<? extends BookingEntry> bookings) {
	return new ArrayList<>(
		bookings.stream().map(b -> b.getElement()).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public DoubleProperty grossEarningsProperty() {
	return this.grossEarnings;
    }

    public double getGrossEarnings() {
	return this.grossEarningsProperty().get();
    }

    public void setGrossEarnings(final double grossEarnings) {
	this.grossEarningsProperty().set(grossEarnings);
    }

    public DoubleProperty netEarningsProperty() {
	return this.netEarnings;
    }

    public double getNetEarnings() {
	return this.netEarningsProperty().get();
    }

    public void setNetEarnings(final double netEarnings) {
	this.netEarningsProperty().set(netEarnings);
    }

    public StringProperty grossEarningsStringProperty() {
	return this.grossEarningsString;
    }

    public String getGrossEarningsString() {
	return this.grossEarningsStringProperty().get();
    }

    public void setGrossEarningsString(final String grossEarningsString) {
	this.grossEarningsStringProperty().set(grossEarningsString);
    }

}

package com.github.drbookings.ui;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.github.drbookings.model.DefaultNetEarningsCalculator;
import com.github.drbookings.model.EarningsProvider;
import com.github.drbookings.model.GrossEarningsProvider;
import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.NetEarningsCalculator;
import com.github.drbookings.model.NetEarningsProvider;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class BookingEntry extends DateRoomEntry<Booking>
	implements NetEarningsProvider, GrossEarningsProvider, EarningsProvider, IBooking {

    public static List<BookingEntry> checkInView(final Collection<? extends BookingEntry> bookings) {
	return bookings.stream().filter(b -> b.isCheckIn()).collect(Collectors.toList());
    }

    public static List<BookingEntry> checkOutView(final Collection<? extends BookingEntry> bookings) {
	return bookings.stream().filter(b -> b.isCheckOut()).collect(Collectors.toList());
    }

    public static Callback<BookingEntry, Observable[]> extractor() {
	return param -> new Observable[] { param.netEarningsProperty(), param.getElement().paymentDoneProperty(),
		param.getElement().welcomeMailSendProperty() };
    }

    public static List<Booking> getBookings(final Collection<? extends BookingEntry> bookings) {
	return new ArrayList<>(
		bookings.stream().map(b -> b.getElement()).collect(Collectors.toCollection(LinkedHashSet::new)));
    }

    public static Set<String> guestNameView(final Collection<? extends BookingEntry> bookings) {
	return bookings.stream().map(b -> b.getElement().getGuest().getName())
		.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public static Set<String> originView(final List<BookingEntry> bookings) {
	return bookings.stream().map(b -> b.getElement().getBookingOrigin().getName())
		.collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * Gross earnings for this particular day.
     */
    private final FloatProperty grossEarnings = new SimpleFloatProperty();

    /**
     * Gross earnings string for this particular day.
     */
    private final StringProperty grossEarningsString = new SimpleStringProperty();

    /**
     * Net earnings for this particular day.
     */
    private final FloatProperty netEarnings = new SimpleFloatProperty();

    public BookingEntry(final LocalDate date, final Booking booking) {
	super(date, booking.getRoom(), booking);
	grossEarningsProperty()
		.bind(Bindings.createObjectBinding(calculateGrossEarnings(), getElement().grossEarningsProperty()));
	netEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), grossEarningsProperty(),
		SettingsManager.getInstance().cleaningFeesProperty()));
    }

    private Callable<Number> calculateGrossEarnings() {
	return () -> {
	    // TODO hard-coded check-out
	    if (isCheckOut()) {
		return 0;
	    }
	    // System.err.println(getElement().getGrossEarnings());
	    // System.err.println(getElement().getNumberOfDays());
	    final double result = getElement().getGrossEarnings() / getElement().getNumberOfNights();
	    // System.err.println(result);
	    return result;
	};
    }

    private Callable<Number> calculateNetEarnings() {

	return () -> {
	    // TODO hard-coded check-out
	    if (isCheckOut()) {
		return 0;
	    }
	    final NetEarningsCalculator c = new DefaultNetEarningsCalculator();
	    c.setFees(SettingsManager.getInstance().getCleaningFees() / getElement().getNumberOfNights());
	    final float result = c.calculateNetEarnings(getGrossEarnings(), getElement().getBookingOrigin().getName());
	    return result;
	};
    }

    @Override
    public float getEarnings(final boolean netEarnings) {
	if (netEarnings) {
	    return getNetEarnings();
	}
	return getGrossEarnings();
    }

    @Override
    public float getGrossEarnings() {
	return this.grossEarningsProperty().get();
    }

    public String getGrossEarningsString() {
	return this.grossEarningsStringProperty().get();
    }

    @Override
    public float getNetEarnings() {
	return this.netEarningsProperty().get();

    }

    @Override
    public FloatProperty grossEarningsProperty() {
	return this.grossEarnings;
    }

    public StringProperty grossEarningsStringProperty() {
	return this.grossEarningsString;
    }

    public boolean isCheckIn() {
	return getDate().equals(getElement().getCheckIn());
    }

    public boolean isCheckOut() {
	return getDate().equals(getElement().getCheckOut());
    }

    @Override
    public FloatProperty netEarningsProperty() {
	return this.netEarnings;
    }

    public void setGrossEarnings(final float grossEarnings) {
	this.grossEarningsProperty().set(grossEarnings);
    }

    public void setGrossEarningsString(final String grossEarningsString) {
	this.grossEarningsStringProperty().set(grossEarningsString);
    }

    public void setNetEarnings(final float netEarnings) {
	this.netEarningsProperty().set(netEarnings);
    }

    @Override
    public String toString() {
	return "date:" + getDate() + ",room:" + getRoom() + ",guest:" + getElement().getGuest();
    }

}

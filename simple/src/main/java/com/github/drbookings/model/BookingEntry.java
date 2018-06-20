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

package com.github.drbookings.model;

import java.time.LocalDate;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.DateRoomEntry;
import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class BookingEntry extends DateRoomEntry<BookingBean>
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

    /**
     * Creates a new {@code BookingEntry} for given date and from given parent.
     *
     * @param date
     *            Date of this {@code BookingEntry}
     * @param booking
     *            parent for this {@code BookingEntry}
     */
    public BookingEntry(final LocalDate date, final BookingBean booking) {
	super(date, booking.getRoom(), booking);
	grossEarningsProperty().bind(Bindings.createObjectBinding(calculateGrossEarnings(),
		getElement().grossEarningsProperty(), SettingsManager.getInstance().showNetEarningsProperty()));
	netEarningsProperty()
		.bind(Bindings.createObjectBinding(calculateNetEarnings(), getElement().netEarningsProperty()));
    }

    private Callable<Number> calculateGrossEarnings() {
	return () -> {
	    if (isCheckOut()) {
		return 0;
	    }
	    final double result = getElement().getGrossEarnings() / getElement().getNumberOfNights();
	    return result;
	};
    }

    private Callable<Number> calculateNetEarnings() {

	return () -> {
	    if (isCheckOut()) {
		return 0;
	    }
	    final NetEarningsCalculator c = new DefaultNetEarningsCalculator();
	    final Number result = c.apply(this);
	    return result;
	};
    }

    @Override
    public BookingOrigin getBookingOrigin() {
	return getElement().getBookingOrigin();
    }

    /**
     * Returns the earnings for this booking entry. That is, the booking earnings
     * per night. All earnings from all booking entries from the same booking are
     * always the same.
     *
     * @param netEarnings
     *            if {@code true}, net earnings will be returned; gross earnings
     *            otherwise
     * @return the earnings for this booking entry
     * @see NetEarningsCalculator
     */
    @Override
    public float getEarnings(final boolean netEarnings) {
	if (netEarnings) {
	    return getNetEarnings();
	}
	return getGrossEarnings();
    }

    @Override
    public float getGrossEarnings() {
	return grossEarningsProperty().get();
    }

    public String getGrossEarningsString() {
	return grossEarningsStringProperty().get();
    }

    @Override
    public float getNetEarnings() {
	return netEarningsProperty().get();

    }

    @Override
    public FloatProperty grossEarningsProperty() {
	return grossEarnings;
    }

    public StringProperty grossEarningsStringProperty() {
	return grossEarningsString;
    }

    public boolean isCheckIn() {
	return getDate().equals(getElement().getCheckIn());
    }

    public boolean isCheckOut() {
	return getDate().equals(getElement().getCheckOut());
    }

    @Override
    public boolean isPaymentDone() {
	return getElement().isPaymentDone();
    }

    public boolean isStay() {
	return !isCheckIn() && !isCheckOut();
    }

    @Override
    public FloatProperty netEarningsProperty() {
	return netEarnings;
    }

    public void setGrossEarnings(final float grossEarnings) {
	grossEarningsProperty().set(grossEarnings);
    }

    public void setGrossEarningsString(final String grossEarningsString) {
	grossEarningsStringProperty().set(grossEarningsString);
    }

    public void setNetEarnings(final float netEarnings) {
	netEarningsProperty().set(netEarnings);
    }

    @Override
    public String toString() {
	return "BookingEntry{" + "date=" + getDate() + ", element=" + getElement() + '}';
    }
}

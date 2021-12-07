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

import com.google.common.base.Objects;
import javafx.beans.Observable;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.MonetaryAmount;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class CleaningEntry implements IDed, DateEntry<Cleaning>, PaymentProvider, OriginProvider, RoomDateProvider {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(CleaningEntry.class);

	public static Callback<CleaningEntry, Observable[]> extractor() {

		return param -> new Observable[]{param.cleaningCostsProperty()};
	}

	@Deprecated
	public static BookingBean getDummyBooking(final BookingOrigin bookingOrigin) {

		return new DummyBooking(bookingOrigin);
	}

	@Deprecated
	public static BookingBean getDummyBooking(final String bookingOrigin) {

		return getDummyBooking(new BookingOrigin(bookingOrigin));
	}

	private boolean black;
	/**
	 * A booking can exist without a cleaning. But a cleaning cannot without a
	 * booking.
	 */
	@Deprecated
	private final BookingBean booking;
	private BookingOrigin bookingOrigin;
	/**
	 * The Google Calendar IDs.
	 */
	private List<String> calendarIds = new ArrayList<>();
	/**
	 * The costs for this cleaning, i.e., the money going to the cleaning person.
	 */
	private final FloatProperty cleaningCosts = new SimpleFloatProperty();
	private final DateEntry<Cleaning> dateEntryDelegate;
	private final IDed idedDelegate;
	private Room room;

	public CleaningEntry(final LocalDate date, final BookingBean booking, final Cleaning element, final boolean black) {

		this(null, date, booking, element, black);
	}

	public CleaningEntry(final LocalDate date, final Cleaning element, final boolean black) {

		this(null, date, null, element, black);
	}

	public CleaningEntry(final LocalDate date, final BookingBean booking, final Cleaning element, final boolean black, final MonetaryAmount cleaningCosts) {

		this(date, booking, element, black);
		setCleaningCosts(cleaningCosts);
	}

	public CleaningEntry(final LocalDate date, final BookingOrigin bookingOrigin, final Cleaning element, final boolean black, final MonetaryAmount cleaningCosts) {

		this(date, getDummyBooking(bookingOrigin), element, black);
		setCleaningCosts(cleaningCosts);
	}

	public CleaningEntry(final LocalDate date, final String bookingOrigin, final Cleaning cleaning, final boolean black, final double cleaningCosts) {

		this(date, bookingOrigin, cleaning, black, Payments.createMondary(cleaningCosts));
	}

	public CleaningEntry(final LocalDate date, final String bookingOrigin, final Cleaning element, final boolean black, final MonetaryAmount cleaningCosts) {

		this(date, getDummyBooking(bookingOrigin), element, black);
		setCleaningCosts(cleaningCosts);
	}

	public CleaningEntry(final String id, final LocalDate date, final BookingBean booking, final Cleaning element, final boolean black) {

		this(id, date, booking, element, black, Payments.createMondary(0));
	}

	@Deprecated
	public CleaningEntry(final String id, final LocalDate date, final BookingBean booking, final Cleaning element, final boolean black, final MonetaryAmount cleaningCosts) {

		this.booking = booking;
		dateEntryDelegate = new DateEntryImpl<>(date, element);
		idedDelegate = new IDedImpl(id);
		this.black = black;
		setCleaningCosts(cleaningCosts);
	}

	public CleaningEntry(final String id, final LocalDate date, final BookingBean booking, final Cleaning cleaning, final boolean isBlack, final String cleaningCosts) {

		this(id, date, booking, cleaning, isBlack, Payments.createMondary(cleaningCosts));
	}

	public CleaningEntry(final String id, final LocalDate date, final Cleaning element, final boolean black, final MonetaryAmount cleaningCosts) {

		booking = null;
		dateEntryDelegate = new DateEntryImpl<>(date, element);
		idedDelegate = new IDedImpl(id);
		this.black = black;
		setCleaningCosts(cleaningCosts);
	}

	public CleaningEntry(final LocalDate date, final Cleaning cleaning, final Room room, final boolean black) {

		this(null, date, null, cleaning, black);
		setRoom(room);
	}

	public void addCalendarId(final String id) {

		calendarIds.add(id);
	}

	public final FloatProperty cleaningCostsProperty() {

		return cleaningCosts;
	}

	@Override
	public boolean equals(final Object obj) {

		if(this == obj)
			return true;
		if(!super.equals(obj))
			return false;
		if(!(obj instanceof CleaningEntry))
			return false;
		final CleaningEntry other = (CleaningEntry)obj;
		return Objects.equal(getBooking(), other.getBooking()) && Objects.equal(getDate(), other.getDate());
	}

	@Deprecated
	public BookingBean getBooking() {

		return booking;
	}

	@Override
	public BookingOrigin getBookingOrigin() {

		return getBooking() == null ? null : getBooking().getBookingOrigin();
	}

	public List<String> getCalendarIds() {

		return calendarIds;
	}

	public final float getCleaningCosts() {

		return cleaningCostsProperty().get();
	}

	@Override
	public LocalDate getDate() {

		return dateEntryDelegate.getDate();
	}

	@Override
	public Cleaning getElement() {

		return dateEntryDelegate.getElement();
	}

	@Override
	public String getId() {

		return idedDelegate.getId();
	}

	public String getName() {

		return getElement().getName();
	}

	@Override
	public List<Payment> getPayments() {

		return Arrays.asList(new PaymentImpl(getDate(), Float.toString(getCleaningCosts())));
	}

	@Override
	public Room getRoom() {

		return room;
	}

	@Override
	public int hashCode() {

		return Objects.hashCode(getBooking(), getDate());
	}

	public boolean isBlack() {

		return black;
	}

	public void setBlack(final boolean b) {

		black = b;
	}

	public void setBookingOrigin(final BookingOrigin bookingOrigin) {

		this.bookingOrigin = bookingOrigin;
	}

	public CleaningEntry setCalendarIds(final Collection<? extends String> calendarIds) {

		if(calendarIds != null) {
			this.calendarIds = new ArrayList<>(calendarIds);
		}
		return this;
	}

	public void setCleaningCosts(final double cleaningCosts) {

		setCleaningCosts(Payments.createMondary(cleaningCosts));
	}

	public final void setCleaningCosts(final MonetaryAmount cleaningCosts) {

		cleaningCostsProperty().set(cleaningCosts.getNumber().floatValue());
	}

	public void setRoom(final Room room) {

		this.room = room;
	}

	@Override
	public String toString() {

		return "CleaningEntry date=" + getDate() + ", element=" + getElement() + ", room=" + getRoom() + ", costs=" + String.format("%4.2f", getCleaningCosts()) + ", tax=" + (!isBlack());
	}
}

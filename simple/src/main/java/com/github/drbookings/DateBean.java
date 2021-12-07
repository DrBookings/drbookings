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
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

/**
 * UI bean representing one row/ day in the 'main' table.
 */
public class DateBean implements Comparable<DateBean> {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

	public static Callback<DateBean, Observable[]> extractor() {

		return param -> new Observable[]{param.selfProperty(), param.roomsProperty(), param.totalEarningsProperty()};
	}

	/**
	 * The date that is represented by this bean.
	 */
	private final LocalDate date;
	/**
	 * The room manifestations that exist on this date.
	 */
	private final ListProperty<RoomBean> rooms = new SimpleListProperty<>(FXCollections.observableArrayList(RoomBean.extractor()));
	/**
	 * Number of rooms/ bookings that have payment received.
	 */
	private final IntegerProperty paymentsReceived = new SimpleIntegerProperty();
	/**
	 * The occupancy that day in percent.
	 *
	 * @deprecated
	 */
	@Deprecated
	private final DoubleProperty occupancy = new SimpleDoubleProperty();
	/**
	 * Sum of all earnings for this day. Can be net or gross earnings, depending on
	 * the settings.
	 *
	 * @deprecated
	 */
	@Deprecated
	private final FloatProperty totalEarnings = new SimpleFloatProperty();
	/**
	 * The sum of all earnings for this day per origin. Can be net or gross
	 * earnings, depending on the settings.
	 *
	 * @deprecated
	 */
	@Deprecated
	private final MapProperty<String, Number> earningsPerOrigin = new SimpleMapProperty<>(FXCollections.observableMap(new TreeMap<>()));
	/**
	 * A reference to {@code this}. Used for change-event listening.
	 */
	private final ObjectProperty<DateBean> self = new SimpleObjectProperty<>();
	/**
	 * Access to all data. Needed for auto-creation of rooms.
	 *
	 * @deprecated
	 */
	@Deprecated
	private final DrBookingsData manager;
	private UICleaningData cleaningData;

	public UICleaningData getCleaningData() {

		return cleaningData;
	}

	public void setCleaningData(final UICleaningData cleaningData) {

		this.cleaningData = cleaningData;
		rooms.forEach(r -> r.setCleaningData(cleaningData));
	}

	@Deprecated
	public DateBean(final LocalDate date, final DrBookingsData manager) {

		Objects.requireNonNull(date);
		Objects.requireNonNull(manager);
		this.date = date;
		this.manager = manager;
		bindProperties();
	}

	public DateBean(final LocalDate date) {

		Objects.requireNonNull(date);
		this.date = date;
		manager = null;
		bindProperties();
	}

	/**
	 * TODO rename this method.
	 *
	 * @return
	 */
	public DoubleProperty auslastungProperty() {

		return occupancy;
	}

	private void bindProperties() {

		selfProperty().bind(Bindings.createObjectBinding(update(), roomsProperty(), paymentsReceivedProperty(), totalEarningsProperty()));
		occupancyProperty().bind(Bindings.createObjectBinding(calculateOccupancy(), roomsProperty()));
		earningsPerOriginProperty().bind(Bindings.createObjectBinding(calculateEarningsPerOrigin(), roomsProperty(), SettingsManager.getInstance().showNetEarningsProperty()));
		totalEarningsProperty().bind(Bindings.createObjectBinding(calculateEarnings(), earningsPerOriginProperty()));
	}

	/**
	 * Calculates the earnings for this day. Can be net or gross earnings, depending
	 * on the settings.
	 *
	 * @return
	 */
	private Callable<Number> calculateEarnings() {

		return () -> {
			return getEarningsPerOrigin().values().stream().mapToDouble(eo -> eo.doubleValue()).sum();
		};
	}

	/**
	 * Calculates the earnings for this day per origin. Can be net or gross
	 * earnings, depending on the settings.
	 *
	 * @return
	 */
	private Callable<ObservableMap<String, Number>> calculateEarningsPerOrigin() {

		return () -> {
			final ObservableMap<String, Number> result = FXCollections.observableMap(new TreeMap<>());
			final Stream<BookingEntry> s = getRooms().stream().filter(e -> e.getBookingEntry() != null).flatMap(r -> r.getFilteredBookingEntry().toList().stream());
			s.forEach(b -> {
				double n = result.getOrDefault(b.getElement().getBookingOrigin().getName(), Double.valueOf(0)).doubleValue();
				n += b.getEarnings(SettingsManager.getInstance().isShowNetEarnings());
				result.put(b.getElement().getBookingOrigin().getName(), n);
			});
			return result;
		};
	}

	private Callable<Number> calculateOccupancy() {

		return () -> {
			return new OccupancyRateCalculator().apply(getRooms());
		};
	}

	/**
	 * Compares by delegating to {@link #getDate()#compareTo(DateBean)}.
	 */
	@Override
	public int compareTo(final DateBean o) {

		return getDate().compareTo(o.getDate());
	}

	public final MapProperty<String, Number> earningsPerOriginProperty() {

		return earningsPerOrigin;
	}

	@Override
	public boolean equals(final Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		final DateBean other = (DateBean)obj;
		if(getDate() == null) {
            return other.getDate() == null;
		} else return getDate().equals(other.getDate());
    }

	public double getAuslastung() {

		return auslastungProperty().get();
	}

	@Deprecated
	public DrBookingsData getData() {

		return manager;
	}

	public LocalDate getDate() {

		return date;
	}

	public final Map<String, Number> getEarningsPerOrigin() {

		return earningsPerOriginProperty().get();
	}

	public double getOccupancy() {

		return occupancyProperty().get();
	}

	public int getPaymentsReceived() {

		return paymentsReceivedProperty().get();
	}

	@Deprecated
	public RoomBean getRoom(final Room name) {

		return getRoom(name.getName());
	}

	/**
	 * Returns a {@link RoomBean} by name. If there is no such element, a new one is
	 * created and added to {@link #roomsProperty()}.
	 *
	 * @return a {@link RoomBean} by name
	 */
	public RoomBean getRoom(final String name) {

		if((name == null) || (name.length() < 1))
			throw new IllegalArgumentException("No name given");
		for(final RoomBean rb : getRooms()) {
			if(rb.getRoom().getName().equals(name))
				return rb;
		}
		// auto-creation of room
		final RoomBean rb = new RoomBean(new Room(name), this);
		rb.setCleaningData(cleaningData);
		roomsProperty().add(rb);
		// if (logger.isDebugEnabled()) {
		// logger.debug("Auto-created room " + rb);
		// }
		return rb;
	}

	public List<RoomBean> getRooms() {

		return roomsProperty().get();
	}

	public DateBean getSelf() {

		return selfProperty().get();
	}

	public double getTotalEarnings() {

		return totalEarningsProperty().get();
	}

	public boolean isEmpty() {

		return getRooms().stream().allMatch(r -> r.isEmpty());
	}

	public DoubleProperty occupancyProperty() {

		return occupancy;
	}

	public IntegerProperty paymentsReceivedProperty() {

		return paymentsReceived;
	}

	public ListProperty<RoomBean> roomsProperty() {

		return rooms;
	}

	public ObjectProperty<DateBean> selfProperty() {

		return self;
	}

	public final void setEarningsPerOrigin(final Map<String, Number> earningsPerOrigin) {

		earningsPerOriginProperty().clear();
		earningsPerOriginProperty().putAll(earningsPerOrigin);
	}

	public void setOccupancy(final double occupancy) {

		occupancyProperty().set(occupancy);
	}

	public void setPaymentsReceived(final int paymentsReceived) {

		paymentsReceivedProperty().set(paymentsReceived);
	}

	public void setRooms(final Collection<? extends RoomBean> rooms) {

		roomsProperty().setAll(rooms);
	}

	@SuppressWarnings("unused")
	private void setSelf(final DateBean self) {

		selfProperty().set(self);
	}

	public void setTotalEarnings(final float totalEarnings) {

		totalEarningsProperty().set(totalEarnings);
	}

	@Override
	public String toString() {

		return "DateBean: Date:" + getDate();
	}

	public FloatProperty totalEarningsProperty() {

		return totalEarnings;
	}

	private Callable<DateBean> update() {

		return () -> {
			// System.err.println("updating");
			return DateBean.this;
		};
	}
}

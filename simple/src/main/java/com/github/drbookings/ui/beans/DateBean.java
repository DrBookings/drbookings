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

package com.github.drbookings.ui.beans;

import com.github.drbookings.model.OccupancyRateCalculator;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.model.BookingEntry;
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
 * UI bean representing one row/ day in the 'main' table. Most importantly, holds references to {@link RoomBean room beans} that day.
 */
public class DateBean implements Comparable<DateBean> {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

    public static Callback<DateBean, Observable[]> extractor() {
        return param -> new Observable[]{param.selfProperty(), param.roomsProperty(), param.totalEarningsProperty()};
    }

    private final LocalDate date;

    private final ListProperty<RoomBean> rooms = new SimpleListProperty<>(
        FXCollections.observableArrayList(RoomBean.extractor()));

    /**
     * Number of rooms/ bookings that have payment received.
     */
    private final IntegerProperty paymentsReceived = new SimpleIntegerProperty();

    /**
     *
     */
    private final DoubleProperty occupancy = new SimpleDoubleProperty();

    /**
     * Sum of all net earnings for this day.
     */
    private final FloatProperty totalEarnings = new SimpleFloatProperty();

    private final MapProperty<String, Number> earningsPerOrigin = new SimpleMapProperty<>(
        FXCollections.observableMap(new TreeMap<>()));

    /**
     *
     */
    private final ObjectProperty<DateBean> self = new SimpleObjectProperty<>();

    private final MainManager manager;

    public DateBean(final LocalDate date, final MainManager manager) {
        Objects.requireNonNull(date);
        Objects.requireNonNull(manager);
        this.date = date;
        this.manager = manager;
        selfProperty().bind(Bindings.createObjectBinding(update(), roomsProperty(), paymentsReceivedProperty(),
            totalEarningsProperty()));
        occupancyProperty().bind(Bindings.createObjectBinding(calculateOccupancy(), roomsProperty()));
        earningsPerOriginProperty().bind(Bindings.createObjectBinding(calculateEarningsPerOrigin(), roomsProperty(),
            SettingsManager.getInstance().showNetEarningsProperty()));
        totalEarningsProperty().bind(Bindings.createObjectBinding(calculateEarnings(), earningsPerOriginProperty()));
    }

    private Callable<ObservableMap<String, Number>> calculateEarningsPerOrigin() {
        return () -> {
            final ObservableMap<String, Number> result = FXCollections.observableMap(new TreeMap<>());
            final Stream<BookingEntry> s = getRooms().stream().flatMap(r -> r.getFilteredBookingEntries().stream());
            s.forEach(b -> {
                double n = result.getOrDefault(b.getElement().getBookingOrigin().getName(), Double.valueOf(0))
                    .doubleValue();
                n += b.getEarnings(SettingsManager.getInstance().isShowNetEarnings());
                result.put(b.getElement().getBookingOrigin().getName(), n);
            });
            return result;
        };
    }

    public DoubleProperty auslastungProperty() {
        return this.occupancy;
    }

    private Callable<Number> calculateEarnings() {

        return () -> {
            return getEarningsPerOrigin().values().stream().mapToDouble(eo -> eo.doubleValue()).sum();

        };
    }

    private Callable<Number> calculateOccupancy() {
        return () -> {
            return new OccupancyRateCalculator().apply(getRooms());
        };
    }

    @Override
    public int compareTo(final DateBean o) {
        return getDate().compareTo(o.getDate());
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DateBean other = (DateBean) obj;
        if (getDate() == null) {
            if (other.getDate() != null) {
                return false;
            }
        } else if (!getDate().equals(other.getDate())) {
            return false;
        }
        return true;
    }

    public double getAuslastung() {
        return this.auslastungProperty().get();
    }

    public LocalDate getDate() {
        return date;
    }

    protected MainManager getManager() {
        return manager;
    }

    public double getOccupancy() {
        return this.occupancyProperty().get();
    }

    public int getPaymentsReceived() {
        return this.paymentsReceivedProperty().get();
    }

    @Deprecated
    public RoomBean getRoom(final Room name) {
//        if (name == null || name.length() < 1) {
//            throw new IllegalArgumentException("No name given");
//        }
        for (final RoomBean rb : getRooms()) {
            if (rb.getRoom().equals(name)) {
                return rb;
            }
        }
        final RoomBean rb = new RoomBean(name, this, getManager());
        roomsProperty().add(rb);
        // if (logger.isDebugEnabled()) {
        // logger.debug("Rooms now " + getRooms());
        // }
        return rb;
    }

    /**
     * Returns a {@link RoomBean} by name. If there is no such element, a new one is created and added to {@link #roomsProperty()}.
     *
     * @param name
     * @return a {@link RoomBean} by name
     */
    public RoomBean getRoom(final String name) {
        if (name == null || name.length() < 1) {
            throw new IllegalArgumentException("No name given");
        }
        for (final RoomBean rb : getRooms()) {
            if (rb.getRoom().getName().equals(name)) {
                return rb;
            }
        }
        final RoomBean rb = new RoomBean(new Room(name), this, getManager());
        roomsProperty().add(rb);
        // if (logger.isDebugEnabled()) {
        // logger.debug("Rooms now " + getRooms());
        // }
        return rb;
    }

    public List<RoomBean> getRooms() {
        return this.roomsProperty().get();
    }

    public DateBean getSelf() {
        return this.selfProperty().get();
    }

    public double getTotalEarnings() {
        return this.totalEarningsProperty().get();
    }

    public boolean isEmpty() {
        return getRooms().stream().allMatch(r -> r.isEmpty());
    }

    public DoubleProperty occupancyProperty() {
        return this.occupancy;
    }

    public IntegerProperty paymentsReceivedProperty() {
        return this.paymentsReceived;
    }

    public ListProperty<RoomBean> roomsProperty() {
        return this.rooms;
    }

    public ObjectProperty<DateBean> selfProperty() {
        return this.self;
    }

    public void setOccupancy(final double occupancy) {
        this.occupancyProperty().set(occupancy);
    }

    public void setPaymentsReceived(final int paymentsReceived) {
        this.paymentsReceivedProperty().set(paymentsReceived);
    }

    public void setRooms(final Collection<? extends RoomBean> rooms) {
        this.roomsProperty().setAll(rooms);
    }

    @SuppressWarnings("unused")
    private void setSelf(final DateBean self) {
        this.selfProperty().set(self);
    }

    public void setTotalEarnings(final float totalEarnings) {
        this.totalEarningsProperty().set(totalEarnings);
    }

    @Override
    public String toString() {
        return "DateBean: Date:" + getDate();
    }

    public FloatProperty totalEarningsProperty() {
        return this.totalEarnings;
    }

    private Callable<DateBean> update() {
        return () -> {
            // System.err.println("updating");
            return DateBean.this;
        };
    }

    public final MapProperty<String, Number> earningsPerOriginProperty() {
        return this.earningsPerOrigin;
    }

    public final Map<String, Number> getEarningsPerOrigin() {
        return this.earningsPerOriginProperty().get();
    }

    public final void setEarningsPerOrigin(final Map<String, Number> earningsPerOrigin) {
        this.earningsPerOriginProperty().clear();
        this.earningsPerOriginProperty().putAll(earningsPerOrigin);
    }

}

package com.github.drbookings.ui.beans;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.OccupancyRateCalculator;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

public class DateBean implements Comparable<DateBean> {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

    public static Callback<DateBean, Observable[]> extractor() {
	return param -> new Observable[] { param.selfProperty(), param.roomsProperty(), param.totalEarningsProperty() };
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
	totalEarningsProperty().bind(Bindings.createObjectBinding(calculateEarnings(), roomsProperty(),
		SettingsManager.getInstance().cleaningFeesProperty(),
		SettingsManager.getInstance().showNetEarningsProperty()));
    }

    public DoubleProperty auslastungProperty() {
	return this.occupancy;
    }

    private Callable<Number> calculateEarnings() {

	return () -> {
	    final List<BookingEntry> bookings = getRooms().stream().flatMap(r -> r.getFilteredBookingEntries().stream())
		    .collect(Collectors.toList());
	    // System.err.println(bookings);
	    final OptionalDouble result = bookings.stream().filter(b -> !b.isCheckOut())
		    .mapToDouble(b -> b.getEarnings(SettingsManager.getInstance().isShowNetEarnings())).average();
	    // System.err.println("net earnings: " + result);
	    if (result.isPresent()) {
		return result.getAsDouble();
	    }
	    return 0;

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

    public RoomBean getRoom(final String name) {
	if (name == null || name.length() < 1) {
	    throw new IllegalArgumentException("No name given");
	}
	for (final RoomBean rb : getRooms()) {
	    if (rb.getName().equals(name)) {
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

}

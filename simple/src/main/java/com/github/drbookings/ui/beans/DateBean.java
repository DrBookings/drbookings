package com.github.drbookings.ui.beans;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

public class DateBean implements Comparable<DateBean> {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

    public static Callback<DateBean, Observable[]> extractor() {
	return param -> new Observable[] { param.selfProperty(), param.roomsProperty(),
		param.totalNetEarningsProperty() };
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
    private final DoubleProperty totalNetEarnings = new SimpleDoubleProperty();

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
		totalNetEarningsProperty()));
	occupancyProperty().bind(Bindings.createObjectBinding(calculateOccupancy(), roomsProperty()));
	totalNetEarningsProperty().bind(Bindings.createObjectBinding(calculateNetEarnings(), roomsProperty(),
		SettingsManager.getInstance().cleaningFeesProperty()));
    }

    private Callable<Number> calculateNetEarnings() {

	return () -> {
	    final List<BookingEntry> bookings = getRooms().stream().flatMap(r -> r.getBookingEntries().stream())
		    .collect(Collectors.toList());
	    // System.err.println(bookings);
	    final double result = bookings.stream().mapToDouble(b -> b.getNetEarnings()).sum();
	    // System.err.println("net earnings: " + result);
	    return result;

	};
    }

    private Callable<Number> calculateOccupancy() {
	return () -> {
	    final int cntRooms = getRooms().size();
	    // TODO: hard coded check out filter
	    final long cntBusyRooms = getRooms().stream().filter(r -> !r.getFilteredBookingEntries().stream()
		    .filter(b -> !b.isCheckOut()).collect(Collectors.toList()).isEmpty()).count();
	    return cntBusyRooms / (double) cntRooms;
	};
    }

    public DoubleProperty auslastungProperty() {
	return this.occupancy;
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
	final RoomBean rb = new RoomBean(name, getDate(), getManager());
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

    public double getTotalNetEarnings() {
	return this.totalNetEarningsProperty().get();
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

    public void setTotalNetEarnings(final double totalNetEarnings) {
	this.totalNetEarningsProperty().set(totalNetEarnings);
    }

    @Override
    public String toString() {
	return "DateBean: Date:" + getDate();
    }

    public DoubleProperty totalNetEarningsProperty() {
	return this.totalNetEarnings;
    }

    private Callable<DateBean> update() {
	return () -> {
	    // System.err.println("updating");
	    return DateBean.this;
	};
    }

    public boolean isEmpty() {
	return getRooms().stream().allMatch(r -> r.isEmpty());
    }

}

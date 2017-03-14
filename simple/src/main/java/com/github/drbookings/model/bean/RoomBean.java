package com.github.drbookings.model.bean;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

public class RoomBean implements Comparable<RoomBean> {

    private final static Logger logger = LoggerFactory.getLogger(RoomBean.class);

    public static Callback<RoomBean, Observable[]> extractor() {
	return param -> new Observable[] { param.bookingsProperty(), param.cleaningProperty() };
    }

    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty cleaning = new SimpleStringProperty();

    private final ListProperty<BookingBean> bookings = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingBean.extractor()));

    private final ObjectProperty<DateBean> dateBean = new SimpleObjectProperty<>();

    /**
     * Internally updated.
     */
    private final BooleanProperty booked = new SimpleBooleanProperty();

    /**
     * Internally updated.
     */
    private final BooleanProperty hasCleaning = new SimpleBooleanProperty();

    /**
     * Internally updated.
     */
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private String id;

    RoomBean() {
	setId(UUID.randomUUID().toString());

	bindBookedProperty();
	bindHasCleaningProperty();
	bindDateProperty();

    }

    RoomBean(final String name) {
	this();
	setName(name);

    }

    public synchronized RoomBean addBooking(final BookingBean booking) throws OverbookingException {

	if (getBookings().contains(booking)) {
	    throw new IllegalArgumentException("Booking already present");
	}
	bookingsProperty().add(booking);
	// isCheckOut() needs a room set
	// if (bookingsProperty().filtered(b -> !b.isCheckOut()).size() > 1)
	// {
	// throw new OverbookingException();
	// }

	booking.setRoom(this);
	return this;
    }

    public synchronized RoomBean addBooking(final RoomBean room, final BookingBean booking)
	    throws OverbookingException {

	if (getBookings().contains(booking)) {
	    throw new IllegalArgumentException("Booking already present");
	} else if (getBookings().size() > 2) {
	    // > 2 over booking in any case.
	    throw new OverbookingException();
	} else {
	    bookingsProperty().add(booking);
	    // isCheckOut() needs a room set
	    if (bookingsProperty().filtered(b -> !b.isCheckOut()).size() > 1) {
		throw new OverbookingException();
	    }
	}
	booking.setRoom(this);
	return this;
    }

    private void bindBookedProperty() {
	bookedProperty().bind(Bindings.createBooleanBinding(
		() -> bookingsProperty().filtered(b -> b.isCheckOut()).size() > 0, bookingsProperty()));
    }

    private void bindDateProperty() {
	dateProperty().bind(Bindings.createObjectBinding(() -> {
	    if (dateBeanProperty().get() != null) {
		return dateBeanProperty().get().getDate();
	    } else {
		return null;
	    }
	}, dateBeanProperty()));

    }

    private void bindHasCleaningProperty() {
	hasCleaningProperty().bind(Bindings.createBooleanBinding(
		() -> cleaningProperty().get() != null && !cleaningProperty().get().isEmpty(), cleaningProperty()));

    }

    public BooleanProperty bookedProperty() {
	return this.booked;
    }

    public ListProperty<BookingBean> bookingsProperty() {
	return this.bookings;
    }

    public StringProperty cleaningProperty() {
	return this.cleaning;
    }

    @Override
    public int compareTo(final RoomBean o) {
	return getDateBean().compareTo(o.getDateBean());
    }

    public RoomBean createCopyFor(final DateBean date) throws OverbookingException {
	final RoomBean result = new RoomBean(getName()).setCleaning(getCleaning());
	date.addRoom(result);
	return result;
    }

    public ObjectProperty<DateBean> dateBeanProperty() {
	return dateBean;
    }

    public ObjectProperty<LocalDate> dateProperty() {
	return this.date;
    }

    public Optional<BookingBean> getBooking(final String guestName) {
	final List<BookingBean> bookings = bookingsProperty().filtered(b -> b.getGuestName().equals(guestName));
	if (bookings.size() > 1) {
	    // if guest checks in and out (different booking source)
	}
	if (bookings.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(bookings.get(0));

    }

    @XmlElement(name = "bookings")
    public List<BookingBean> getBookings() {
	return this.bookingsProperty().get();
    }

    public Optional<BookingBean> getCheckIn() {
	return getDateBean().getDataModel().getCheckIn(this);
    }

    public Optional<BookingBean> getCheckOut() {
	return getDateBean().getDataModel().getCheckOut(this);
    }

    @XmlElement
    public String getCleaning() {
	return this.cleaningProperty().get();
    }

    public LocalDate getDate() {
	return this.dateProperty().get();
    }

    @XmlElement(name = "date")
    @XmlIDREF
    public DateBean getDateBean() {
	return dateBeanProperty().get();
    }

    @XmlID
    @XmlAttribute
    public String getId() {
	return id;
    }

    @XmlElement(name = "name")
    public String getName() {
	return this.nameProperty().get();
    }

    public boolean hasCheckIn() {
	return getDateBean().getDataModel().hasCheckIn(this);
    }

    public boolean hasCheckOut() {
	return getDateBean().getDataModel().hasCheckOut(this);
    }

    public boolean hasCleaning() {
	return hasCleaningProperty().get();
    }

    public BooleanProperty hasCleaningProperty() {
	return this.hasCleaning;
    }

    public boolean isBooked() {
	return this.bookedProperty().get();
    }

    public void merge(final RoomBean room) throws OverbookingException {
	final List<BookingBean> bookings = room.getBookings();
	for (final BookingBean bb : bookings) {
	    addBooking(bb);
	}
    }

    public StringProperty nameProperty() {
	return this.name;
    }

    public synchronized RoomBean removeBooking(final BookingBean booking) {
	booking.setRoom(null);
	bookingsProperty().remove(booking);
	return this;
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setBooked(final boolean booked) {
	this.bookedProperty().set(booked);
    }

    public void setBookings(final Collection<? extends BookingBean> bookings) {
	this.bookingsProperty().setAll(bookings);
    }

    public RoomBean setCleaning(final String cleaning) {
	this.cleaningProperty().set(cleaning);
	return this;
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setDate(final LocalDate date) {
	this.dateProperty().set(date);
    }

    /**
     * Set by DateBean itself.
     */
    public RoomBean setDateBean(final DateBean dateBean) {
	this.dateBeanProperty().set(dateBean);
	return this;
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setHasCleaning(final boolean hasCleaning) {
	this.hasCleaningProperty().set(hasCleaning);
    }

    private void setId(final String id) {
	this.id = id;
    }

    public RoomBean setName(final String name) {
	this.nameProperty().set(name);
	return this;
    }

    @Override
    public String toString() {
	return "Room: booking:" + getBookings() + ", cleaning:" + getCleaning() + ", name:" + getName();
    }

}

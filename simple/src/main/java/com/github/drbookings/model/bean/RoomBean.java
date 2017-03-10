package com.github.drbookings.model.bean;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.DataModel;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

public class RoomBean implements Comparable<RoomBean> {

    private final static Logger logger = LoggerFactory.getLogger(RoomBean.class);

    public static Callback<RoomBean, Observable[]> extractor() {
	return param -> new Observable[] { param.bookingsProperty() };
    }

    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty cleaning = new SimpleStringProperty();

    private final ListProperty<BookingBean> bookings = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingBean.extractor()));

    private DateBean dateBean;

    private String id;

    public RoomBean() {
	this.id = UUID.randomUUID().toString();

    }

    public synchronized RoomBean addBooking(final BookingBean booking) {
	booking.setRoomBean(this);
	if (!bookingsProperty().contains(booking)) {
	    bookingsProperty().add(booking);
	}
	return this;
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

    public Optional<RoomBean> getAfter() {
	return DataModel.getInstance().getAfter(this);
    }

    public Optional<BookingBean> getBooking(final String guestName) {
	final List<BookingBean> bookings = getBookings().stream().filter(b -> b.getGuestName().equals(guestName))
		.collect(Collectors.toList());
	if (bookings.size() > 1) {
	    if (logger.isWarnEnabled()) {
		logger.warn("Unexpected booking count for guest " + guestName + ", " + bookings);
	    }
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

    @XmlElement
    public String getCleaning() {
	return this.cleaningProperty().get();
    }

    @XmlElement(name = "date")
    @XmlIDREF
    public DateBean getDateBean() {
	return dateBean;
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
	for (final BookingBean bb : bookings) {
	    if (bb.isCheckIn()) {
		return true;
	    }
	}
	return false;
    }

    public boolean hasCleaning() {
	return getCleaning() != null && !getCleaning().isEmpty();
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
	booking.setRoomBean(null);
	bookingsProperty().remove(booking);
	return this;
    }

    public void setBookings(final Collection<? extends BookingBean> bookings) {
	this.bookingsProperty().setAll(bookings);
    }

    public RoomBean setCleaning(final String cleaning) {
	this.cleaningProperty().set(cleaning);
	return this;
    }

    public RoomBean setDateBean(final DateBean dateBean) {
	this.dateBean = dateBean;
	return this;
    }

    public void setId(final String id) {
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

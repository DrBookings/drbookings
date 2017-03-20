package com.github.drbookings.model.bean;

import java.time.LocalDate;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class BookingBean implements Comparable<BookingBean> {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(BookingBean.class);

    public static Callback<BookingBean, Observable[]> extractor() {
	return param -> new Observable[] { param.guestNameProperty(), param.sourceProperty(),
		param.bruttoEarningsProperty() };
    }

    private String id;

    /**
     * Not null initially to match regex
     */
    private final StringProperty source = new SimpleStringProperty("");

    private final StringProperty guestName = new SimpleStringProperty();

    private final FloatProperty bruttoEarnings = new SimpleFloatProperty();

    /**
     * Internally updated.
     */
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    /**
     * Set by the room itself.
     */
    private final ObjectProperty<RoomBean> room = new SimpleObjectProperty<>();

    /**
     * Internally updated.
     */
    private final FloatProperty bruttoEarningsPerNight = new SimpleFloatProperty();

    protected BookingBean() {
	setId(UUID.randomUUID().toString());
	bindBruttoEarningsPerNightProperty();
	bindDateProperty();
    }

    public BookingBean(final BookingBean booking) {
	this(booking.getGuestName(), booking.getGuestName());
    }

    public BookingBean(final String guestName) {
	this();
	setGuestName(guestName);
    }

    public BookingBean(final String guestName, final String roomName) {
	this(guestName);
	setRoom(new RoomBean(roomName, this));
    }

    private void bindBruttoEarningsPerNightProperty() {
	bruttoEarningsPerNight
		.bind(Bindings.createFloatBinding(() -> calculateBruttoEarningsPerNight(), bruttoEarningsProperty()));

    }

    private void bindDateProperty() {
	date.bind(Bindings.createObjectBinding(() -> getDateFromRoom(), roomProperty()));

    }

    public FloatProperty bruttoEarningsPerNightProperty() {
	return this.bruttoEarningsPerNight;
    }

    public FloatProperty bruttoEarningsProperty() {
	return this.bruttoEarnings;
    }

    private float calculateBruttoEarningsPerNight() {
	if (getRoom() == null) {
	    return 0;
	}
	return getRoom().getDateBean().getDataModel().calculateBruttoEarningsPerNight(this);
    }

    @Override
    public int compareTo(final BookingBean o) {
	if (getDate() == null) {
	    throw new IllegalStateException("Booking without date " + this);
	}
	return getDate().compareTo(o.getDate());
    }

    public ObjectProperty<LocalDate> dateProperty() {
	return date;
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
	final BookingBean other = (BookingBean) obj;
	if (getGuestName() == null) {
	    if (other.getGuestName() != null) {
		return false;
	    }
	} else if (!getGuestName().equals(other.getGuestName())) {
	    return false;
	}
	if (getSource() == null) {
	    if (other.getSource() != null) {
		return false;
	    }
	} else if (!getSource().equals(other.getSource())) {
	    return false;
	}
	if (getDate() == null) {
	    if (other.getDate() != null) {
		return false;
	    }
	} else if (!getDate().equals(other.getDate())) {
	    return false;
	}
	return true;
    }

    @XmlElement(name = "brutto-earnings")
    public float getBruttoEarnings() {
	return this.bruttoEarningsProperty().get();
    }

    public float getBruttoEarningsPerNight() {
	return this.bruttoEarningsPerNightProperty().get();
    }

    public LocalDate getDate() {
	return dateProperty().get();
    }

    private LocalDate getDateFromRoom() {
	if (getRoom() == null) {
	    return null;
	}
	return getRoom().getDate();
    }

    @XmlElement
    public String getGuestName() {
	return this.guestNameProperty().get();
    }

    @XmlID
    @XmlAttribute
    public String getId() {
	return id;
    }

    public int getNumberOfTotalNights() {
	if (getRoom() == null) {
	    return 0;
	}
	return getRoom().getDateBean().getDataModel().getNightCount(this);
    }

    @XmlElement(name = "room")
    @XmlIDREF
    public RoomBean getRoom() {
	return roomProperty().get();
    }

    @XmlElement(name = "source")
    public String getSource() {
	return this.sourceProperty().get();
    }

    public StringProperty guestNameProperty() {
	return this.guestName;
    }

    public boolean hasGuest() {
	return getGuestName() != null && !getGuestName().isEmpty();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (getGuestName() == null ? 0 : getGuestName().hashCode());
	result = prime * result + (getSource() == null ? 0 : getSource().hashCode());
	result = prime * result + (getDate() == null ? 0 : getDate().hashCode());
	return result;
    }

    public boolean isCheckIn() {
	if (getRoom() == null || getRoom().getDateBean() == null || getRoom().getDateBean().getDataModel() == null) {
	    return false;
	}
	return getRoom().getDateBean().getDataModel().isCheckIn(this);
    }

    public boolean isCheckOut() {
	if (getRoom() == null || getRoom().getDateBean() == null || getRoom().getDateBean().getDataModel() == null) {
	    return false;
	}
	return getRoom().getDateBean().getDataModel().isCheckOut(this);
    }

    public boolean isConnected(final BookingBean otherBooking) {
	return isForwardConnected(otherBooking) || isReverseConnected(otherBooking);
    }

    private boolean isForwardConnected(final BookingBean otherBooking) {
	if (getDate() == null) {
	    return false;
	}
	if (!otherBooking.getGuestName().equals(getGuestName())) {
	    return false;
	}
	final boolean result = getDate().equals(otherBooking.getDate().plusDays(1));
	return result;
    }

    private boolean isReverseConnected(final BookingBean otherBooking) {
	if (getDate() == null) {
	    return false;
	}
	if (!otherBooking.getGuestName().equals(getGuestName())) {
	    return false;
	}
	final boolean result = getDate().equals(otherBooking.getDate().minusDays(1));
	return result;
    }

    public ObjectProperty<RoomBean> roomProperty() {
	return room;
    }

    public void setAllBruttoEarnings(final float earnings) {
	getRoom().getDateBean().getDataModel().setAllBruttoEarnings(this, earnings);

    }

    public void setBruttoEarnings(final float earnings) {
	this.bruttoEarningsProperty().set(earnings);
    }

    /**
     * Property is bound.
     */
    @SuppressWarnings("unused")
    private void setBruttoEarningsPerNight(final float bruttoEarningsPerNight) {
	this.bruttoEarningsPerNightProperty().set(bruttoEarningsPerNight);
    }

    /**
     * Property is bound.
     */
    @SuppressWarnings("unused")
    private BookingBean setDate(final LocalDate date) {
	this.dateProperty().set(date);
	return this;
    }

    public BookingBean setGuestName(final String guestName) {
	this.guestNameProperty().set(guestName);
	return this;
    }

    private void setId(final String id) {
	this.id = id;
    }

    public BookingBean setRoom(final RoomBean roomBean) {
	this.roomProperty().set(roomBean);
	return this;
    }

    public BookingBean setSource(final String source) {
	this.sourceProperty().set(source);
	return this;
    }

    public StringProperty sourceProperty() {
	return this.source;
    }

    @Override
    public String toString() {
	final StringBuilder sb = new StringBuilder("Booking:");
	if (getRoom() != null) {
	    sb.append("room:");
	    sb.append(getRoom().getName());
	    sb.append(", ");
	    if (getRoom().getDate() != null) {
		sb.append("date: ");
		sb.append(getRoom().getDate());
		sb.append(", ");
	    }
	}
	sb.append("guest:");
	sb.append(getGuestName());
	sb.append(", source:");
	sb.append(getSource());
	return sb.toString();
    }

}

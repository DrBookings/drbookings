package com.github.drbookings.model.bean;

import java.util.Optional;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.DataModel;

import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class BookingBean implements Comparable<BookingBean> {

    private static final Logger logger = LoggerFactory.getLogger(BookingBean.class);

    public static Callback<BookingBean, Observable[]> extractor() {
	return param -> new Observable[] { param.guestNameProperty(), param.sourceProperty() };
    }

    private String id;

    private final StringProperty source = new SimpleStringProperty();

    private final StringProperty guestName = new SimpleStringProperty();

    private RoomBean roomBean;

    public BookingBean() {
	this.id = UUID.randomUUID().toString();
    }

    @Override
    public int compareTo(final BookingBean o) {
	return getRoomBean().getDateBean().compareTo(o.getRoomBean().getDateBean());
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
	return true;
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

    @XmlElement(name = "room")
    @XmlIDREF
    public RoomBean getRoomBean() {
	return roomBean;
    }

    @XmlElement
    public String getSource() {
	return this.sourceProperty().get();
    }

    public StringProperty guestNameProperty() {
	return this.guestName;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (getGuestName() == null ? 0 : getGuestName().hashCode());
	result = prime * result + (getSource() == null ? 0 : getSource().hashCode());
	return result;
    }

    public boolean isCheckIn() {
	final Optional<BookingBean> optionalBeforeThis = DataModel.getInstance().getBefore(this);

	if (optionalBeforeThis.isPresent()) {
	    final BookingBean beforeThis = optionalBeforeThis.get();
	    if (beforeThis.getGuestName() == null || beforeThis.getGuestName().isEmpty()
		    || !beforeThis.getGuestName().equals(getGuestName())) {
		return true;
	    }
	} else {
	    return true;
	}
	return false;
    }

    public boolean isCheckOut() {
	final Optional<BookingBean> optionalAfterThis = DataModel.getInstance().getAfter(this);
	if (optionalAfterThis.isPresent()) {
	    final BookingBean afterThis = optionalAfterThis.get();
	    if (afterThis.getGuestName() == null || afterThis.getGuestName().isEmpty()
		    || !afterThis.getGuestName().equals(getGuestName())) {
		return true;
	    } else {
		return false;
	    }
	} else {
	    return true;
	}
    }

    public BookingBean setGuestName(final String guestName) {
	this.guestNameProperty().set(guestName);
	return this;
    }

    public void setId(final String id) {
	this.id = id;
    }

    public void setRoomBean(final RoomBean roomBean) {
	this.roomBean = roomBean;
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
	return "Booking: Date: " + getRoomBean().getDateBean().getDate() + " Guest:" + getGuestName() + ", Source:"
		+ getSource();
    }

}

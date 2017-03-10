package com.github.drbookings.model.bean;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.LocalDateAdapter;
import com.github.drbookings.OverbookingException;

import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.util.Callback;

public class DateBean implements Iterable<RoomBean>, Comparable<DateBean> {

    private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

    public static Callback<DateBean, Observable[]> extractor() {
	return param -> new Observable[] { param.roomsProperty(), param.dateProperty() };
    }

    private String id;

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private final ListProperty<RoomBean> rooms = new SimpleListProperty<>(
	    FXCollections.observableArrayList(RoomBean.extractor()));

    public DateBean() {
	this.id = UUID.randomUUID().toString();
	rooms.add(new RoomBean().setName("1").setDateBean(this));
	rooms.add(new RoomBean().setName("2").setDateBean(this));
	rooms.add(new RoomBean().setName("3").setDateBean(this));
	rooms.add(new RoomBean().setName("4").setDateBean(this));
    }

    public DateBean(final LocalDate date) {
	this();
	setDate(date);
    }

    @Override
    public int compareTo(final DateBean o) {
	return getDate().compareTo(o.getDate());
    }

    public ObjectProperty<LocalDate> dateProperty() {
	return this.date;
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

    @XmlElement(name = "date")
    @XmlJavaTypeAdapter(value = LocalDateAdapter.class)
    public LocalDate getDate() {
	return this.dateProperty().get();
    }

    @XmlID
    @XmlAttribute
    public String getId() {
	return id;
    }

    public RoomBean getRoom(final String name) {
	final List<RoomBean> rooms = getRooms().stream().filter(r -> r.getName().equalsIgnoreCase(name))
		.collect(Collectors.toList());
	if (rooms.size() > 1) {
	    if (logger.isErrorEnabled()) {
		logger.error("Too many rooms with id " + name + " (" + rooms + ")");
	    }
	}
	return rooms.get(0);
    }

    @XmlElementWrapper(name = "rooms")
    @XmlElement(name = "room")
    public List<RoomBean> getRooms() {
	return this.roomsProperty().get();
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (getDate() == null ? 0 : getDate().hashCode());
	return result;
    }

    @Override
    public Iterator<RoomBean> iterator() {
	return roomsProperty().iterator();
    }

    public void merge(final DateBean b) throws OverbookingException {
	for (final RoomBean room : b.getRooms()) {
	    final RoomBean room2 = getRoom(room.getName());
	    room2.merge(room);
	}
    }

    public ListProperty<RoomBean> roomsProperty() {
	return this.rooms;
    }

    public void setDate(final LocalDate date) {
	this.dateProperty().set(date);
    }

    public void setId(final String id) {
	this.id = id;
    }

    public void setRooms(final Collection<? extends RoomBean> rooms) {
	System.err.println("Setting rooms");
	this.roomsProperty().setAll(rooms);
    }

    @Override
    public String toString() {
	return "DateBean: Date:" + getDate() + ", Rooms:" + getRooms();
    }

}

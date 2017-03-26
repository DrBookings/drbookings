package com.github.drbookings.model.bean;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.Rooms;
import com.github.drbookings.model.manager.DataModel;
import com.github.drbookings.ser.LocalDateAdapter;

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
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class DateBean implements Iterable<RoomBean>, Comparable<DateBean> {

    private static final Logger logger = LoggerFactory.getLogger(DateBean.class);

    public static Callback<DateBean, Observable[]> extractor() {
	return param -> new Observable[] { param.selfProperty() };
    }

    private String id;

    private final ObjectProperty<DataModel> dataModel = new SimpleObjectProperty<>();

    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private final ListProperty<RoomBean> rooms = new SimpleListProperty<>(
	    FXCollections.observableArrayList(RoomBean.extractor()));

    /**
     * Bound property.
     */
    private final IntegerProperty paymentsReceived = new SimpleIntegerProperty();

    /**
     * Bound property.
     */
    private final DoubleProperty auslastung = new SimpleDoubleProperty();

    /**
     * Bound property.
     */
    private final DoubleProperty totalEarningsPerDay = new SimpleDoubleProperty();

    /**
     * Bound property.
     */
    private final IntegerProperty roomCount = new SimpleIntegerProperty();

    /**
     * Bound property.
     */
    private final ObjectProperty<DateBean> self = new SimpleObjectProperty<>();

    /**
     * Bound property.
     */
    private final ListProperty<BookingBean> bookings = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingBean.extractor()));

    DateBean() {
	setId(UUID.randomUUID().toString());
	bindAuslastungProperty();
	bindEarningsPerDayProperty();
	bindRoomCountProperty();
	bindBookingsProperty();
	bindSelfProperty();
	rooms.add(new RoomBean("1").setDateBean(this));
	rooms.add(new RoomBean("2").setDateBean(this));
	rooms.add(new RoomBean("3").setDateBean(this));
	rooms.add(new RoomBean("4").setDateBean(this));
    }

    private void bindSelfProperty() {
	selfProperty().bind(Bindings.createObjectBinding(update(), roomsProperty(), dateProperty(), bookingsProperty(),
		totalEarningsPerDayProperty()));

    }

    public DateBean(final LocalDate date) {
	Objects.requireNonNull(date);
	setId(UUID.randomUUID().toString());
	bindAuslastungProperty();
	bindPaymentsReceivedProperty();
	bindEarningsPerDayProperty();
	bindRoomCountProperty();
	bindBookingsProperty();
	bindSelfProperty();
	setDate(date);
	rooms.add(new RoomBean("1").setDateBean(this));
	rooms.add(new RoomBean("2").setDateBean(this));
	rooms.add(new RoomBean("3").setDateBean(this));
	rooms.add(new RoomBean("4").setDateBean(this));
    }

    private void bindPaymentsReceivedProperty() {
	paymentsReceivedProperty().bind(Bindings.createIntegerBinding(countPayments(), bookingsProperty()));

    }

    private Callable<Integer> countPayments() {
	return () -> (int) bookingsProperty().stream().filter(b -> b.isMoneyReceived()).count();
    }

    public synchronized DateBean addRoom(final RoomBean room) throws OverbookingException {
	final Optional<RoomBean> rb = getRoom(room.getName());
	if (rb.isPresent()) {
	    rooms.remove(rb.get());
	    rb.get().setDateBean(null);
	    room.setDateBean(this);
	    room.addBookings(rb.get().getAllBookings());
	    rooms.add(room);
	    // if (logger.isDebugEnabled()) {
	    // logger.debug(getDate() + " room merged " + rb.get() + ", now: " +
	    // rooms);
	    // }
	} else {
	    room.setDateBean(this);
	    rooms.add(room);
	    // if (logger.isDebugEnabled()) {
	    // logger.debug(getDate() + " room added " + room + ", now: " +
	    // rooms);
	    // }
	}

	return this;
    }

    public void applyGuestNameFilter(final String guestNameFilterString) {
	for (final RoomBean rb : rooms) {
	    rb.setGuestNameFilterString(guestNameFilterString);
	}

    }

    public DoubleProperty auslastungProperty() {
	return this.auslastung;
    }

    private void bindAuslastungProperty() {
	auslastungProperty().bind(Bindings.createFloatBinding(() -> {
	    float result1 = 0;
	    for (final RoomBean rb : rooms) {
		result1 += rb.filteredBookingsProperty().filtered(new NightCountFilter()).size();
	    }
	    if (result1 == 0) {
		return result1;
	    }
	    final float result2 = result1 / roomsProperty().size();
	    return result2;
	}, roomsProperty()));

    }

    private void bindEarningsPerDayProperty() {
	totalEarningsPerDayProperty().bind(Bindings.createFloatBinding(calculateEarningsPerDay(), selfProperty()));

    }

    private void bindRoomCountProperty() {
	roomCountProperty().bind(Bindings.createIntegerBinding(() -> roomsProperty().size(), roomsProperty()));

    }

    private void bindBookingsProperty() {
	bookings.bind(Bindings.createObjectBinding(updateBookings(), roomsProperty()));
    }

    private Callable<ObservableList<BookingBean>> updateBookings() {

	return () -> {
	    final ObservableList<BookingBean> result = FXCollections
		    .observableArrayList(Rooms.bookingsView(getRooms()));
	    // if (logger.isDebugEnabled()) {
	    // logger.debug("Updating bookings to " + result);
	    // }
	    return result;
	};
    }

    /**
     * Internally updated.
     */
    private ListProperty<BookingBean> bookingsProperty() {
	return this.bookings;
    }

    private Callable<Float> calculateEarningsPerDay() {
	return () -> {
	    float result = 0;

	    for (final BookingBean bb : getBookings()) {
		result += bb.getNettoEarningsPerNight();
	    }

	    // if (logger.isDebugEnabled()) {
	    // logger.debug("Calculated earnings, now " + result);
	    // }

	    return result;
	};
    }

    @Override
    public int compareTo(final DateBean o) {
	return getDate().compareTo(o.getDate());
    }

    public ObjectProperty<DataModel> dataModelProperty() {
	return this.dataModel;
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

    public double getAuslastung() {
	return this.auslastungProperty().get();
    }

    public List<BookingBean> getBookings() {
	return this.bookingsProperty().get();
    }

    @XmlElement(name = "data-model")
    @XmlIDREF
    public DataModel getDataModel() {
	return this.dataModelProperty().get();
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

    public Optional<RoomBean> getRoom(final String name) {
	for (final RoomBean room : rooms) {
	    if (room.getName().equals(name)) {
		return Optional.of(room);
	    }
	}
	return Optional.empty();
    }

    public int getRoomCount() {
	return this.roomCountProperty().get();
    }

    @XmlElementWrapper(name = "rooms")
    @XmlElement(name = "room")
    public List<RoomBean> getRooms() {
	// if (logger.isDebugEnabled()) {
	// logger.debug(getDate() + " Returning rooms " +
	// this.roomsProperty().get());
	// }
	return this.roomsProperty().get();
    }

    public DateBean getSelf() {
	return this.selfProperty().get();
    }

    public double getTotalEarningsPerNight() {
	return this.totalEarningsPerDayProperty().get();
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
	    final Optional<RoomBean> room2 = getRoom(room.getName());
	    if (room2.isPresent()) {
		room2.get().addBookings(room.getAllBookings());
	    } else {
		addRoom(new RoomBean(room));
	    }
	}
    }

    public IntegerProperty roomCountProperty() {
	return this.roomCount;
    }

    public ListProperty<RoomBean> roomsProperty() {
	return this.rooms;
    }

    public ObjectProperty<DateBean> selfProperty() {
	return this.self;
    }

    /**
     * Set by {@link DataModel} itself.
     */
    public DateBean setDataModel(final DataModel dataModel) {
	this.dataModelProperty().set(dataModel);
	return this;
    }

    private void notifyChange() {
	for (final RoomBean rb : rooms) {
	    rb.setDateBean(this);
	}
    }

    private DateBean setDate(final LocalDate date) {
	this.dateProperty().set(date);
	// System.err.println("Date set to " + date);
	return this;
    }

    private void setId(final String id) {
	this.id = id;
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setRoomCount(final int roomCount) {
	this.roomCountProperty().set(roomCount);
    }

    public void setRooms(final Collection<? extends RoomBean> rooms) {
	this.roomsProperty().setAll(rooms);
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setSelf(final DateBean self) {
	this.selfProperty().set(self);
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setTotalEarningsPerDay(final double totalEarnings) {
	this.totalEarningsPerDayProperty().set(totalEarnings);
    }

    @Override
    public String toString() {
	return "DateBean: Date:" + getDate() + ", Rooms:" + getRooms();
    }

    public DoubleProperty totalEarningsPerDayProperty() {
	return this.totalEarningsPerDay;
    }

    private Callable<DateBean> update() {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Updaing self: " + getDate());
	// }
	// notifyChange();
	return () -> DateBean.this;
    }

    public synchronized void addBooking(final BookingBean booking) throws OverbookingException {
	final RoomBean rb = booking.getRoom();
	if (rb == null) {
	    throw new IllegalArgumentException("Booking has no room " + booking);
	}
	final Optional<RoomBean> rb2 = getRoom(rb.getName());
	if (rb2.isPresent()) {
	    rb2.get().addBooking(booking);
	} else {
	    addRoom(rb);
	}
    }

    public IntegerProperty paymentsReceivedProperty() {
	return this.paymentsReceived;
    }

    public int getPaymentsReceived() {
	return this.paymentsReceivedProperty().get();
    }

    public void setPaymentsReceived(final int paymentsReceived) {
	this.paymentsReceivedProperty().set(paymentsReceived);
    }

}

package com.github.drbookings.model.bean;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlIDREF;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.ui.BookingFilter;

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
import javafx.collections.ObservableList;
import javafx.util.Callback;

public class RoomBean extends WarnableBean implements Comparable<RoomBean> {

    private final static Logger logger = LoggerFactory.getLogger(RoomBean.class);

    public static Callback<RoomBean, Observable[]> extractor() {
	return param -> new Observable[] { param.cleaningProperty(), param.filteredBookingsProperty(),
		param.allBookingsProperty(), param.warningProperty(), param.dateProperty(), param.dateBeanProperty() };
    }

    private final StringProperty name = new SimpleStringProperty();

    private final StringProperty cleaning = new SimpleStringProperty();

    private final StringProperty bookingFilter = new SimpleStringProperty();

    private final ListProperty<BookingBean> allBookings = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingBean.extractor()));
    /**
     * Bound property.
     */
    private final ListProperty<BookingBean> filteredBookings = new SimpleListProperty<>(
	    FXCollections.observableArrayList(BookingBean.extractor()));

    private final ObjectProperty<DateBean> dateBean = new SimpleObjectProperty<>();

    /**
     * Bound property.
     */
    private final BooleanProperty booked = new SimpleBooleanProperty();

    /**
     * Bound property.
     */
    private final BooleanProperty needsCleaning = new SimpleBooleanProperty(Boolean.FALSE);

    /**
     * Bound property.
     */
    private final BooleanProperty hasCleaning = new SimpleBooleanProperty();

    /**
     * Bound property.
     */
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    private String id;

    protected RoomBean() {

	init();
	setId(UUID.randomUUID().toString());
	bindBookedProperty();
	bindHasCleaningProperty();
	bindDateProperty();
	bindNeedsCleaningProperty();
	bindFilteredBookingsProperty();

    }

    public RoomBean(final RoomBean room) {
	this();
	setName(room.getName());
	setCleaning(room.getCleaning());
    }

    public RoomBean(final String name) {
	this();
	setName(name);

    }

    public RoomBean(final String roomName, final BookingBean bookingBean) {
	this(roomName);
	addBookingNoCheck(bookingBean);
    }

    public synchronized RoomBean addBooking(final BookingBean booking) throws OverbookingException {

	if (allBookingsProperty().contains(booking)) {
	    if (!hasCheckOut()) {
		throw new OverbookingException("Cannot add same booking again " + getDate());
	    } else {
		// silently ignore
		// return this;
	    }
	}
	if (allBookingsProperty().filtered(b -> b.isCheckIn()).size() > 0
		|| allBookingsProperty().filtered(b -> !b.isCheckOut()).size() > 0) {
	    throw new OverbookingException("Cannot add " + booking + " to room " + this);
	}
	addBookingNoCheck(booking);
	return this;
    }

    public synchronized RoomBean addBooking(final RoomBean room, final BookingBean booking)
	    throws OverbookingException {

	if (getAllBookings().contains(booking)) {
	    throw new IllegalArgumentException("Booking already present");
	}
	allBookingsProperty().add(booking);
	booking.setRoom(this);

	return this;
    }

    private synchronized void addBookingNoCheck(final BookingBean booking) {

	allBookingsProperty().add(booking);
	booking.setRoom(this);

    }

    public ListProperty<BookingBean> allBookingsProperty() {
	return this.allBookings;
    }

    private void bindBookedProperty() {
	bookedProperty().bind(Bindings.createBooleanBinding(
		() -> filteredBookingsProperty().filtered(new NightCountFilter()).size() > 0,
		filteredBookingsProperty()));
    }

    private void bindDateProperty() {
	dateProperty().bind(Bindings.createObjectBinding(() -> {
	    if (dateBeanProperty().get() != null) {
		// if (logger.isDebugEnabled()) {
		// logger.debug(getName() + " updating date to " +
		// dateBeanProperty().get().getDate());
		// }
		return dateBeanProperty().get().getDate();

	    } else {
		return null;
	    }
	}, dateBeanProperty()));

    }

    private void bindFilteredBookingsProperty() {
	filteredBookingsProperty().bind(
		Bindings.createObjectBinding(updateFilteredBookings(), allBookingsProperty(), bookingFilterProperty()));

    }

    private void bindHasCleaningProperty() {
	hasCleaningProperty().bind(Bindings.createBooleanBinding(
		() -> cleaningProperty().get() != null && !cleaningProperty().get().isEmpty(), cleaningProperty()));

    }

    private void bindNeedsCleaningProperty() {
	needsCleaningProperty().bind(Bindings.createBooleanBinding(calculateNeedsCleaning(), filteredBookingsProperty(),
		cleaningProperty()));

    }

    public BooleanProperty bookedProperty() {
	return this.booked;
    }

    public StringProperty bookingFilterProperty() {
	return this.bookingFilter;
    }

    private Callable<Boolean> calculateNeedsCleaning() {
	return () -> {
	    if (RoomBean.this.hasCleaning()) {
		return false;
	    }
	    if (RoomBean.this.hasCheckOut() && RoomBean.this.hasCheckIn()) {
		return !RoomBean.this.getCheckIn().get().getGuestName()
			.equals(RoomBean.this.getCheckOut().get().getGuestName());
	    }
	    if (!RoomBean.this.hasCheckOut()) {
		return false;
	    }
	    return searchNextCleaning();
	};

    }

    @Override
    protected Callable<Boolean> calculateWarningProperty() {
	return () -> {
	    if (getDate() != null && getDate().isBefore(LocalDate.now().minusDays(7))) {
		// timeout
		return false;
	    }
	    boolean result = isNeedsCleaning();
	    if (!result) {
		result = getFilteredBookings().stream().filter(b -> !b.isCheckOut()).count() > 1;
	    }
	    if (!result) {
		result = getFilteredBookings().stream().anyMatch(b -> b.isWarning());
	    }
	    return result;
	};
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

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof RoomBean)) {
	    return false;
	}
	final RoomBean other = (RoomBean) obj;
	if (getName() == null) {
	    if (other.getName() != null) {
		return false;
	    }
	} else if (!getName().equals(other.getName())) {
	    return false;
	}
	return true;
    }

    public ListProperty<BookingBean> filteredBookingsProperty() {
	return this.filteredBookings;
    }

    @XmlElement
    public List<BookingBean> getAllBookings() {
	return this.allBookingsProperty().get();
    }

    public String getBookingFilterString() {
	return this.bookingFilterProperty().get();
    }

    public Optional<BookingBean> getCheckIn() {
	if (getDateBean() == null) {
	    return Optional.empty();
	}
	return getDateBean().getDataModel().getCheckIn(this);
    }

    public Optional<BookingBean> getCheckOut() {
	if (getDateBean() == null) {
	    return Optional.empty();
	}
	return getDateBean().getDataModel().getCheckOut(this);
    }

    @XmlElement
    public String getCleaning() {
	return this.cleaningProperty().get();
    }

    public Optional<BookingBean> getConnectedBooking(final BookingBean bookingBean) {
	final List<BookingBean> bookings = allBookingsProperty().filtered(new ConnectedBookingFilter(bookingBean));
	if (bookings.size() > 1) {
	    // if guest checks in and out (different booking source)
	}
	if (bookings.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(bookings.get(0));
    }

    public Optional<RoomBean> getConnectedNext() {
	if (getDateBean() == null) {
	    return Optional.empty();
	}
	return getDateBean().getDataModel().getConnectedNext(this);
    }

    public LocalDate getDate() {
	return this.dateProperty().get();
    }

    @XmlElement
    @XmlIDREF
    public DateBean getDateBean() {
	return dateBeanProperty().get();
    }

    public List<BookingBean> getFilteredBookings() {
	return this.filteredBookingsProperty().get();
    }

    @XmlID
    @XmlAttribute
    public String getId() {
	return id;
    }

    @XmlElement
    public String getName() {
	return this.nameProperty().get();
    }

    @Override
    protected Observable[] getWarnableObservables() {
	return new Observable[] { needsCleaningProperty() };
    }

    public boolean hasCheckIn() {
	if (getDateBean() == null) {
	    return false;
	}
	return getDateBean().getDataModel().hasCheckIn(this);
    }

    public boolean hasCheckOut() {
	if (getDateBean() == null) {
	    return false;
	}
	if (getDateBean().getDataModel() == null) {
	    return false;
	}
	return getDateBean().getDataModel().hasCheckOut(this);
    }

    public boolean hasCleaning() {
	return hasCleaningProperty().get();
    }

    public BooleanProperty hasCleaningProperty() {
	return this.hasCleaning;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (getAllBookings() == null ? 0 : getAllBookings().hashCode());
	result = prime * result + (getCleaning() == null ? 0 : getCleaning().hashCode());
	result = prime * result + (getName() == null ? 0 : getName().hashCode());
	return result;
    }

    public boolean isBooked() {
	return this.bookedProperty().get();
    }

    public boolean isNeedsCleaning() {
	return this.needsCleaningProperty().get();
    }

    public StringProperty nameProperty() {
	return this.name;
    }

    public BooleanProperty needsCleaningProperty() {
	return this.needsCleaning;
    }

    public synchronized RoomBean removeBooking(final BookingBean booking) {
	booking.setRoom(null);
	allBookingsProperty().remove(booking);
	return this;
    }

    private Boolean searchNextCleaning() {
	Optional<RoomBean> next = getConnectedNext();
	while (next.isPresent()) {
	    final RoomBean rb = next.get();
	    if (rb.hasCleaning()) {
		return false;
	    }
	    if (rb.hasCheckIn()) {
		return true;
	    }
	    next = rb.getConnectedNext();
	}
	return true;
    }

    public synchronized void setAllBookings(final Collection<? extends BookingBean> bookings) {
	this.allBookingsProperty().setAll(bookings);
    }

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setBooked(final boolean booked) {
	this.bookedProperty().set(booked);
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
    public synchronized RoomBean setDateBean(final DateBean dateBean) {
	this.dateBeanProperty().set(dateBean);
	return this;
    }

    public void setGuestNameFilterString(final String guestNameFilterString) {
	this.bookingFilterProperty().set(guestNameFilterString);
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

    /**
     * Bound property.
     */
    @SuppressWarnings("unused")
    private void setNeedsCleaning(final boolean needsCleaning) {
	this.needsCleaningProperty().set(needsCleaning);
    }

    @Override
    public String toString() {
	return getClass().getSimpleName() + "@" + hashCode() + ":date:" + getDate() + ",name:" + getName()
		+ ",bookings:" + getAllBookings().size() + ",cleaning:" + getCleaning();
    }

    private Callable<ObservableList<BookingBean>> updateFilteredBookings() {
	return () -> FXCollections.observableArrayList(allBookingsProperty().stream()
		.filter(new BookingFilter(getBookingFilterString())).collect(Collectors.toList()));
    }

    public void addBookings(final Collection<? extends BookingBean> bookings) throws OverbookingException {
	for (final BookingBean bb : bookings) {
	    addBooking(new BookingBean(bb));
	}

    }

}

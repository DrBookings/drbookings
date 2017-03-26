package com.github.drbookings.model.bean;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.Callable;

import javax.xml.bind.annotation.XmlTransient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.Booking;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Callback;

public class BookingBean extends WarnableBean implements Comparable<BookingBean> {

    public Booking getBookingParent() {
	return bookingParent;
    }

    public void setBookingParent(final Booking bookingParent) {
	this.bookingParent = bookingParent;
    }

    private static final Logger logger = LoggerFactory.getLogger(BookingBean.class);

    public static Callback<BookingBean, Observable[]> extractor() {
	return param -> new Observable[] { param.guestNameProperty(), param.sourceProperty(),
		param.grossEarningsProperty(), param.welcomeMailSendProperty(), param.nettoEarningsProperty(),
		param.warningProperty(), param.roomProperty(), param.moneyReceivedProperty() };
    }

    /**
     * Not null initially to match regex
     */
    private final StringProperty source = new SimpleStringProperty("");

    private final StringProperty guestName = new SimpleStringProperty();

    private final StringProperty checkInNote = new SimpleStringProperty();

    private final DoubleProperty grossEarnings = new SimpleDoubleProperty();

    private final BooleanProperty welcomeMailSend = new SimpleBooleanProperty();

    private final BooleanProperty moneyReceived = new SimpleBooleanProperty();

    private Booking bookingParent;

    /**
     * Bound property.
     */
    private final DoubleProperty nettoEarnings = new SimpleDoubleProperty();

    /**
     * Bound property.
     */
    private final DoubleProperty nettoEarningsPerNight = new SimpleDoubleProperty();

    /**
     * Bound property.
     */
    private final ObjectProperty<LocalDate> date = new SimpleObjectProperty<>();

    /**
     * Bound property.
     */
    private final ObjectProperty<RoomBean> room = new SimpleObjectProperty<>();

    /**
     * Internally updated.
     */
    private final DoubleProperty bruttoEarningsPerNight = new SimpleDoubleProperty();

    private final DoubleProperty serviceFee = new SimpleDoubleProperty(Float.valueOf(0));

    public static BookingBean create(final String guestName, final String roomName, final LocalDate date) {
	if (guestName == null || guestName.length() < 1) {
	    throw new IllegalArgumentException("For guest name");
	}
	if (roomName == null || roomName.length() < 1) {
	    throw new IllegalArgumentException("For room name");
	}
	Objects.requireNonNull(date);
	final DateBean db = new DateBean(date);
	final RoomBean rb = new RoomBean(roomName);
	final BookingBean bb = new BookingBean(guestName);
	try {
	    db.addRoom(rb);
	    rb.addBooking(bb);
	} catch (final Exception e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
	// if (logger.isDebugEnabled()) {
	// logger.debug("Build booking: " + bb);
	// }
	return bb;
    }

    BookingBean(final String guestName) {
	this();
	setGuestName(guestName);
    }

    private BookingBean() {
	init();
	bindBruttoEarningsPerNightProperty();
	bindNettoEarningsProperty();
	bindNettoEarningsPerNightProperty();
	bindDateProperty();

    }

    public BookingBean(final BookingBean template) {
	this();
	setBruttoEarnings(template.getGrossEarnings());
	setCheckInNote(template.getCheckInNote());
	setGuestName(template.getGuestName());
	setRoom(new RoomBean(template.getRoom()));
	setServiceFee(template.getServiceFee());
	setSource(template.getSource());
	setWelcomeMailSend(template.isWelcomeMailSend());
    }

    private void bindBruttoEarningsPerNightProperty() {
	bruttoEarningsPerNight
		.bind(Bindings.createDoubleBinding(() -> calculateBruttoEarningsPerNight(), grossEarningsProperty()));

    }

    private void bindDateProperty() {
	date.bind(Bindings.createObjectBinding(() -> getDateFromRoom(), roomProperty()));

    }

    private void bindNettoEarningsPerNightProperty() {
	nettoEarningsPerNight
		.bind(Bindings.createDoubleBinding(calculateNettoEarningsPerNight(), nettoEarningsProperty()));

    }

    private void bindNettoEarningsProperty() {
	nettoEarnings.bind(Bindings.createDoubleBinding(calculateNettoEarnings(), grossEarningsProperty()));

    }

    public DoubleProperty bruttoEarningsPerNightProperty() {
	return this.bruttoEarningsPerNight;
    }

    public DoubleProperty grossEarningsProperty() {
	return this.grossEarnings;
    }

    private double calculateBruttoEarningsPerNight() {
	if (getRoom() == null) {
	    return 0;
	}
	if (getRoom().getDateBean() == null) {
	    return 0;
	}
	if (getRoom().getDateBean().getDataModel() == null) {
	    return 0;
	}
	return getRoom().getDateBean().getDataModel().calculateBruttoEarningsPerNight(this);
    }

    private Callable<Double> calculateNettoEarnings() {
	return () -> {
	    double result = getGrossEarnings()/* - 60 */;
	    if (getSource().matches(BookingBeans.getRegexAirbnb())) {
		result = result - getServiceFee();
	    } else if (getSource().matches(BookingBeans.getRegexBooking())) {
		result = result - result * 0.12;
	    }
	    // if (logger.isDebugEnabled()) {
	    // logger.debug("Gross earnings changed, calculated net new
	    // earnings: " + result);
	    // }
	    return result;
	};
    }

    private Callable<Double> calculateNettoEarningsPerNight() {
	return () -> {
	    if (getRoom() == null) {
		return 0d;
	    }
	    if (getRoom().getDateBean() == null) {
		return 0d;
	    }
	    if (getRoom().getDateBean().getDataModel() == null) {
		return 0d;
	    }
	    return getRoom().getDateBean().getDataModel().calculateNettoEarningsPerNight(BookingBean.this);
	};
    }

    @Override
    protected Callable<Boolean> calculateWarningProperty() {
	return () -> {
	    if (getDate() != null && getDate().isBefore(LocalDate.now().minusDays(31))) {
		// warning outdated
		return false;
	    }
	    if (!isWelcomeMailSend()) {
		return true;
	    }
	    if (getGrossEarnings() <= 0) {
		return true;
	    }
	    // all ok, no warining
	    return false;
	};
    }

    public StringProperty checkInNoteProperty() {
	return this.checkInNote;
    }

    @Override
    public int compareTo(final BookingBean o) {
	if (getDate() == null) {
	    if (logger.isWarnEnabled()) {
		logger.warn("Booking without date " + this);
	    }
	    return 0;
	}
	if (o.getDate() == null) {
	    if (logger.isWarnEnabled()) {
		logger.warn("Booking without date " + 0);
	    }
	    return 0;
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

    public double getGrossEarnings() {
	return this.grossEarningsProperty().get();
    }

    @XmlTransient
    public double getBruttoEarningsPerNight() {
	return this.bruttoEarningsPerNightProperty().get();
    }

    public String getCheckInNote() {
	return this.checkInNoteProperty().get();
    }

    public LocalDate getDate() {
	return dateProperty().get();
    }

    private LocalDate getDateFromRoom() {
	if (getRoom() == null) {
	    return null;
	}
	final LocalDate result = getRoom().getDate();
	// if (logger.isDebugEnabled()) {
	// logger.debug("Updating date to " + result);
	// }
	return result;
    }

    public String getGuestName() {
	return this.guestNameProperty().get();
    }

    @XmlTransient
    public double getNettoEarnings() {
	return this.nettoEarningsProperty().get();
    }

    @XmlTransient
    public double getNettoEarningsPerNight() {
	return this.nettoEarningsPerNightProperty().get();
    }

    @XmlTransient
    public int getNumberOfTotalNights() {
	if (getRoom() == null) {
	    return 0;
	}
	return getRoom().getDateBean().getDataModel().getNightCount(this);
    }

    @XmlTransient
    public RoomBean getRoom() {
	return roomProperty().get();
    }

    public double getServiceFee() {
	return this.serviceFeeProperty().get();
    }

    public String getSource() {
	return this.sourceProperty().get();
    }

    @Override
    protected Observable[] getWarnableObservables() {
	return new Observable[] { welcomeMailSendProperty(), grossEarningsProperty() };
    }

    public StringProperty guestNameProperty() {
	return this.guestName;
    }

    public boolean hasCheckInNote() {
	return getCheckInNote() != null && getCheckInNote().length() > 0;
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

    @XmlTransient
    public boolean isCheckIn() {
	if (getRoom() == null || getRoom().getDateBean() == null || getRoom().getDateBean().getDataModel() == null) {
	    return false;
	}
	return getRoom().getDateBean().getDataModel().isCheckIn(this);
    }

    @XmlTransient
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

    public boolean isWelcomeMailSend() {
	return this.welcomeMailSendProperty().get();
    }

    public DoubleProperty nettoEarningsPerNightProperty() {
	return this.nettoEarningsPerNight;
    }

    public DoubleProperty nettoEarningsProperty() {
	return this.nettoEarnings;
    }

    public ObjectProperty<RoomBean> roomProperty() {
	return room;
    }

    public DoubleProperty serviceFeeProperty() {
	return this.serviceFee;
    }

    public void setAllBruttoEarnings(final double earnings) {
	getRoom().getDateBean().getDataModel().setAllBruttoEarnings(this, earnings);

    }

    public void setAllCheckInNote(final String checkInNote) {
	getRoom().getDateBean().getDataModel().setAllCheckInNote(this, checkInNote);

    }

    public void setAllWelcomeMailSent(final boolean sent) {
	getRoom().getDateBean().getDataModel().setAllWelcomeMailSend(this, sent);

    }

    public void setBruttoEarnings(final double earnings) {
	this.grossEarningsProperty().set(earnings);
    }

    /**
     * Property is bound.
     */
    @SuppressWarnings("unused")
    private void setBruttoEarningsPerNight(final double bruttoEarningsPerNight) {
	this.bruttoEarningsPerNightProperty().set(bruttoEarningsPerNight);
    }

    public void setCheckInNote(final String checkInNote) {
	this.checkInNoteProperty().set(checkInNote);
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

    public void setNettoEarnings(final double nettoEarnings) {
	this.nettoEarningsProperty().set(nettoEarnings);
    }

    public void setNettoEarningsPerNight(final double nettoEarningsPerNight) {
	this.nettoEarningsPerNightProperty().set(nettoEarningsPerNight);
    }

    public BookingBean setRoom(final RoomBean roomBean) {
	this.roomProperty().set(roomBean);
	// if (logger.isDebugEnabled()) {
	// logger.debug(this.getClass().getSimpleName() + "@" + hashCode() + "
	// room set to " + roomBean);
	// }
	return this;
    }

    public void setServiceFee(final double serviceFee) {
	this.serviceFeeProperty().set(serviceFee);
    }

    public BookingBean setSource(final String source) {
	this.sourceProperty().set(source);
	return this;
    }

    public void setWelcomeMailSend(final boolean welcomeMailSend) {
	this.welcomeMailSendProperty().set(welcomeMailSend);
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

    public BooleanProperty welcomeMailSendProperty() {
	return this.welcomeMailSend;
    }

    public BooleanProperty moneyReceivedProperty() {
	return this.moneyReceived;
    }

    public boolean isMoneyReceived() {
	return this.moneyReceivedProperty().get();
    }

    public void setAllPaymentDone(final boolean moneyReceived) {
	getRoom().getDateBean().getDataModel().setAllPaymentDone(this, moneyReceived);
    }

    public void setPaymentDone(final boolean moneyReceived) {
	this.moneyReceivedProperty().set(moneyReceived);

    }

}

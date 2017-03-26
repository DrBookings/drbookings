package com.github.drbookings.ser;

import javax.xml.bind.Unmarshaller.Listener;

import javafx.application.Platform;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;

public class UnmarshallListener extends Listener {

    // private static final Logger logger =
    // LoggerFactory.getLogger(UnmarshallListener.class);

    private final LongProperty dateCount = new SimpleLongProperty(0);

    private final LongProperty bookingCount = new SimpleLongProperty(0);

    @Override
    public void afterUnmarshal(final Object target, final Object parent) {
	super.afterUnmarshal(target, parent);
	if (target instanceof BookingBeanSer) {
	    Platform.runLater(() -> incrementBookingCount());
	}
    }

    @Override
    public void beforeUnmarshal(final Object target, final Object parent) {
	super.beforeUnmarshal(target, parent);

    }

    public synchronized LongProperty bookingCountProperty() {
	return this.bookingCount;
    }

    public synchronized LongProperty dateCountProperty() {
	return this.dateCount;
    }

    public synchronized long getBookingCount() {
	return this.bookingCountProperty().get();
    }

    public synchronized long getDateCount() {
	return this.dateCountProperty().get();
    }

    private synchronized void incrementBookingCount() {
	setBookingCount(getBookingCount() + 1);
    }

    private synchronized void incrementDateCount() {
	setDateCount(getDateCount() + 1);
    }

    private synchronized void setBookingCount(final long bookingCount) {
	this.bookingCountProperty().set(bookingCount);

    }

    private synchronized void setDateCount(final long dateCount) {
	this.dateCountProperty().set(dateCount);
    }

}

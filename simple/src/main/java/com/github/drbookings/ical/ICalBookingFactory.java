package com.github.drbookings.ical;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.BookingFactory;
import com.github.drbookings.io.BookingParser;
import com.github.drbookings.model.ser.BookingBeanSer;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;

public class ICalBookingFactory implements BookingFactory {

    private static final Logger logger = LoggerFactory.getLogger(ICalBookingFactory.class);

    private final BookingParser iCalParser;

    private final File file;

    public ICalBookingFactory(final File file, final BookingParser iCalParser) {
	super();
	this.iCalParser = iCalParser;
	this.file = file;
    }

    @Override
    public Collection<BookingBeanSer> build() throws IOException {
	final Collection<BookingBeanSer> result = new ArrayList<>();
	final List<ICalendar> icals = Biweekly.parse(file).all();
	for (final ICalendar ical : icals) {
	    for (final VEvent e : ical.getEvents()) {
		try {
		    result.add(processEvent(e));
		} catch (final Exception ex) {
		    if (logger.isInfoEnabled()) {
			logger.info("Failed to process event ", e);
		    }
		    // ex.printStackTrace();
		}
	    }
	}
	return result;
    }

    private BookingBeanSer processEvent(final VEvent e) throws IOException {
	final LocalDate checkIn = iCalParser.getCheckInDate(e);
	final LocalDate checkOut = iCalParser.getCheckOutDate(e);
	final String guestName = iCalParser.getGuestName(e);
	final String id = iCalParser.getExternalID(e);
	final String roomName = iCalParser.getRoomName(e);
	if (checkIn == null || checkOut == null || guestName == null || roomName == null) {
	    throw new NullPointerException();
	}
	final BookingBeanSer b = new BookingBeanSer();
	b.externalId = id;
	b.checkInDate = checkIn;
	b.checkOutDate = checkOut;
	b.guestName = guestName;
	b.roomName = roomName;
	b.source = "Airbnb";
	return b;

    }

}

package com.github.drbookings.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.model.ser.CleaningBeanSer;
import com.github.drbookings.ui.controller.CleaningEntry;

public class XMLStorage {

    public UnmarshallListener getListener() {
	return listener;
    }

    public XMLStorage setListener(final UnmarshallListener listener) {
	this.listener = listener;
	return this;
    }

    private final MainManager manager;
    private static Logger logger = LoggerFactory.getLogger(XMLStorage.class);
    private File file;
    private UnmarshallListener listener;

    public XMLStorage(final MainManager manager) {
	this.manager = manager;
    }

    public void makeBackup() {
	if (file.exists() && file.length() != 0) {
	    try {
		final File backupFile = new File(file.getParentFile(), file.getName() + ".bak");
		FileUtils.copyFile(file, backupFile);
		if (logger.isDebugEnabled()) {
		    logger.debug("Backup created as " + backupFile);
		}
	    } catch (final IOException e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	}
    }

    public void save() throws InterruptedException, ExecutionException {
	doSave();

    }

    public static long countElements(final String tagName, final File file)
	    throws SAXException, IOException, ParserConfigurationException {

	final InputStream in = new FileInputStream(file);
	final SAXParserFactory spf = SAXParserFactory.newInstance();
	final SAXParser saxParser = spf.newSAXParser();
	final AtomicInteger counter = new AtomicInteger();
	saxParser.parse(in, new DefaultHandler() {
	    @Override
	    public void startElement(final String uri, final String localName, final String qName,
		    final Attributes attributes) {
		if (qName.equals(tagName)) {
		    counter.incrementAndGet();
		}
	    }
	});
	return counter.longValue();
    }

    protected void doLoad()
	    throws OverbookingException, SAXException, IOException, ParserConfigurationException, JAXBException {
	if (logger.isDebugEnabled()) {
	    logger.debug("Loading data from " + file);
	}
	final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
	final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();
	if (listener != null) {
	    jaxbMarshaller.setListener(listener);
	}
	final DataStore ds = (DataStore) jaxbMarshaller.unmarshal(file);
	for (final BookingBeanSer bb : (Iterable<BookingBeanSer>) () -> ds.getBookingsSer().stream()
		.sorted((b1, b2) -> b1.checkInDate.compareTo(b2.checkInDate)).iterator()) {
	    final Booking b = manager.addBooking(bb.bookingId, bb.checkInDate, bb.checkOutDate, bb.guestName,
		    bb.roomName, bb.source);
	    // b.setGrossEarnings(bb.grossEarnings);
	    b.setGrossEarningsExpression(bb.grossEarningsExpression);
	    b.setWelcomeMailSend(bb.welcomeMailSend);
	    b.setCheckInNote(bb.checkInNote);
	    b.setPaymentDone(bb.paymentDone);
	}
	for (final CleaningBeanSer cb : ds.getCleaningsSer()) {
	    manager.addCleaning(cb.date, cb.name, cb.room);
	}
    }

    private static final String defaultDataFileNameKey = "data";

    private static final String defaultFileName = "bookings.xml";

    protected void doSave() {
	final Preferences userPrefs = Preferences.userNodeForPackage(getClass());
	final String dir = userPrefs.get(defaultDataFileNameKey, System.getProperty("user.home"));
	file = new File(dir, defaultFileName);
	makeBackup();
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving data to " + file);
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
	    final Marshaller jaxbMarshaller = jc.createMarshaller();
	    jaxbMarshaller.setListener(new MarshallListener());
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.marshal(buildDataStore(manager), file);
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	    userPrefs.remove(defaultDataFileNameKey);
	    return;
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state successful");
	}
	userPrefs.put(defaultDataFileNameKey, dir);
    }

    public static DataStore buildDataStore(final MainManager manager) {
	final DataStore ds = new DataStore();
	for (final Booking b : manager.getBookings()) {
	    ds.getBookingsSer().add(DataStore.transform(b));
	}
	for (final CleaningEntry e : manager.getCleaningEntries()) {
	    ds.getCleaningsSer().add(DataStore.transform(e));
	}
	return ds;
    }

    public void load(final File file)
	    throws OverbookingException, SAXException, IOException, ParserConfigurationException, JAXBException {
	this.file = file;
	doLoad();
    }

}

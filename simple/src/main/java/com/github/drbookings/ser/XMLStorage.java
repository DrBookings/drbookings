package com.github.drbookings.ser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.github.drbookings.ui.CleaningEntry;

public class XMLStorage {

    public UnmarshallListener getListener() {
	return listener;
    }

    public XMLStorage setListener(final UnmarshallListener listener) {
	this.listener = listener;
	return this;
    }

    private static Logger logger = LoggerFactory.getLogger(XMLStorage.class);

    private UnmarshallListener listener;

    public void makeBackup(final File file) {
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

    public void save(final MainManager manager, final File file) throws InterruptedException, ExecutionException {
	doSave(manager, file);

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

    protected DataStore doLoad(final File file)
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
	return ds;
    }

    protected void doSave(final MainManager manager, final File file) {
	makeBackup(file);
	if (logger.isInfoEnabled()) {
	    logger.info("Saving data to " + file);
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
	    final Marshaller jaxbMarshaller = jc.createMarshaller();
	    jaxbMarshaller.setListener(new MarshallListener());
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.marshal(buildDataStore(manager), file);
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	    return;
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state successful");
	}
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

    public DataStore load(final File file)
	    throws OverbookingException, SAXException, IOException, ParserConfigurationException, JAXBException {
	return doLoad(file);
    }

}

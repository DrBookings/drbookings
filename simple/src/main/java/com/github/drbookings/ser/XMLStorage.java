/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.ser;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.io.Backup;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.ui.CleaningEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

public class XMLStorage {

	public UnmarshallListener getListener() {
		return listener;
	}

	public XMLStorage setListener(final UnmarshallListener listener) {
		this.listener = listener;
		return this;
	}

    private static final Logger logger = LoggerFactory.getLogger(XMLStorage.class);

	private UnmarshallListener listener;


    public static void doSave(final Collection<? extends BookingBean> bookings, final File file) {
        Backup.make(file);
        try {
            save(buildDataStore(bookings), file);
        } catch (final Exception e1) {
            logger.error(e1.getLocalizedMessage(), e1);
            return;
        }
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

    public static void save(BookingBean booking, File file) throws JAXBException {
        final JAXBContext jc = JAXBContext.newInstance(BookingBeanSer.class);
        final Marshaller jaxbMarshaller = jc.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(DataStore.transform(booking), file);
    }

	protected void doSave(final MainManager manager, final File file) {
        Backup.make(file);
        try {
			save(buildDataStore(manager), file);
		} catch (final Exception e1) {
			logger.error(e1.getLocalizedMessage(), e1);
			return;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Saving state successful");
		}
	}

    public static DataStore buildDataStore(final Collection<? extends BookingBean> bookings) {
        final DataStore ds = new DataStore();
        for (final BookingBean b : bookings) {
            ds.getBookingsSer().add(DataStore.transform(b));
        }
        return ds;
    }

	public static DataStore buildDataStore(final MainManager manager) {
		final DataStore ds = new DataStore();
        for (final BookingBean b : manager.getBookings()) {
			ds.getBookingsSer().add(DataStore.transform(b));
		}
		for (final CleaningEntry e : manager.getCleaningEntries()) {
			ds.getCleaningsSer().add(DataStore.transform(e));
		}
		return ds;
	}

    public static void save(final DataStore ds, final File file) throws Exception {
        if (logger.isInfoEnabled()) {
            logger.info("Saving to " + file);
        }
        final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
        final Marshaller jaxbMarshaller = jc.createMarshaller();
        jaxbMarshaller.setListener(new MarshallListener());
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(ds, file);

    }

    public void save(final MainManager manager, final File file) {
        doSave(manager, file);

    }

    protected DataStore doLoad(final File file)
            throws JAXBException {
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

	public DataStore load(final File file)
			throws OverbookingException, SAXException, IOException, ParserConfigurationException, JAXBException {
		return doLoad(file);
	}

}

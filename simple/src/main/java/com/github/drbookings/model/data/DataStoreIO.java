package com.github.drbookings.model.data;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ser.DataStoreCore;
import com.github.drbookings.ser.MarshallListener;
import com.github.drbookings.ser.UnmarshallListener;

public class DataStoreIO {
    private UnmarshallListener listener;
    private static final Logger logger = LoggerFactory.getLogger(DataStoreIO.class);

    public void writeToFile(final DataStoreCore ds, final File file) throws Exception {

	final JAXBContext jc = JAXBContext.newInstance(DataStoreCore.class);
	final Marshaller jaxbMarshaller = jc.createMarshaller();
	jaxbMarshaller.setListener(new MarshallListener());
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.marshal(ds, file);
	if (logger.isInfoEnabled()) {
	    logger.info("Wrote to " + file);
	}

    }

    public DataStoreCore readFromFile(final File file) throws JAXBException {

	final JAXBContext jc = JAXBContext.newInstance(DataStoreCore.class);
	final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();
	if (listener != null) {
	    jaxbMarshaller.setListener(listener);
	}
	final DataStoreCore ds = (DataStoreCore) jaxbMarshaller.unmarshal(file);
	if (logger.isDebugEnabled()) {
	    logger.debug("Loaded data from " + file);
	}
	return ds;

    }

}

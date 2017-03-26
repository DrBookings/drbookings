package com.github.drbookings.model;

import java.io.File;
import java.time.LocalDate;
import java.util.Arrays;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.MarshallListener;

public class DataStoreTest {

    private DataStore data;

    private static final File file = new File("src/test/resources/test-data.xml");

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
	data = null;
	// file.delete();
    }

    @Test
    public void testMarshal01() throws Exception {

	data = new DataStore();
	final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
	final Marshaller jaxbMarshaller = jc.createMarshaller();
	jaxbMarshaller.setListener(new MarshallListener());
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.marshal(data, file);
    }

    @Test
    public void testMarshal02() throws Exception {

	data = new DataStore(Arrays.asList(BookingBean.create("testGuest", "testRoom", LocalDate.now())));
	final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
	final Marshaller jaxbMarshaller = jc.createMarshaller();
	jaxbMarshaller.setListener(new MarshallListener());
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.marshal(data, file);
    }

}

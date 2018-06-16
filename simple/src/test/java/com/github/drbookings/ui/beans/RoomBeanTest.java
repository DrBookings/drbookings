package com.github.drbookings.ui.beans;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.model.data.DrBookingsDataImpl;
import com.github.drbookings.model.data.Room;

public class RoomBeanTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    private RoomBean r;

    private DrBookingsDataImpl data;

    @Before
    public void setUp() throws Exception {
	data = new DrBookingsDataImpl();
	r = null;
    }

    @After
    public void tearDown() throws Exception {
	r = null;
	data = null;
    }

    @Test
    public void test00() {

	final DateBean date = new DateBean(LocalDate.of(2018, 05, 02), data);
	r = new RoomBean(new Room("r1"), date);

    }

}

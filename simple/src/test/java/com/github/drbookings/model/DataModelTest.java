package com.github.drbookings.model;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.manager.DataModel;

public class DataModelTest {

    private DataModel dm;

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
    }

    @Test
    public void testAddDateBean01() throws OverbookingException {
	dm = new DataModel();
	final BookingBean bb = BookingBean.create("gestName", "1", LocalDate.now()).setSource("soruce");
	final DateBean db = dm.add(bb);
	assertEquals(4, db.getRooms().size());
	assertEquals(4, db.getRoomCount());
	assertEquals(1, db.getBookings().size());
	assertEquals(bb, db.getBookings().get(0));
    }

}

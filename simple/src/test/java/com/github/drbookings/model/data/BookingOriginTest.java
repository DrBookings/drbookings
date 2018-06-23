package com.github.drbookings.model.data;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class BookingOriginTest {

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
	o1 = null;
	o2 = null;
    }

    private BookingOrigin o1;
    private BookingOrigin o2;

    @Test
    public void testEqualsHashCode01() {
	o1 = new BookingOrigin("booking");
	o2 = new BookingOrigin("booking");

	assertThat(o1, is(o2));
	assertThat(o1.hashCode(), is(o2.hashCode()));
    }

}

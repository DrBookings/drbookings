package com.github.drbookings;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.time.LocalDate;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Range;

public class LocalDatesTest {

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
    public void testGetNumberOfDaysNights01() {
	final LocalDate d1 = LocalDate.of(2015, 05, 15);
	final LocalDate d2 = LocalDate.of(2015, 05, 18);

	assertThat(LocalDates.getNumberOfDays(d1, d2), is(4L));
	assertThat(LocalDates.getNumberOfNights(d1, d2), is(3L));
    }

    @Test
    public void testNumberOfMonths01() {
	final LocalDate d1 = LocalDate.of(2015, 05, 15);
	final LocalDate d2 = LocalDate.of(2015, 05, 15);
	final Range<LocalDate> r = Range.closed(d1, d2);
	assertThat(LocalDates.getNumberOfMonth(r), is(1L));
    }

    @Test
    public void testNumberOfMonths02() {
	final LocalDate d1 = LocalDate.of(2015, 05, 15);
	final LocalDate d2 = LocalDate.of(2015, 06, 01);
	final Range<LocalDate> r = Range.closed(d1, d2);
	assertThat(LocalDates.getNumberOfMonth(r), is(1L));
    }

    @Test
    public void testNumberOfMonths03() {
	final LocalDate d1 = LocalDate.of(2015, 05, 15);
	final LocalDate d2 = LocalDate.of(2015, 06, 15);
	final Range<LocalDate> r = Range.closed(d1, d2);
	assertThat(LocalDates.getNumberOfMonth(r), is(2L));
    }

}

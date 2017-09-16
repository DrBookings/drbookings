package com.github.drbookings.model;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.Room;
import com.github.drbookings.ui.BookingEntry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NightlyRateViewTest {

    @Before
    public void setUp() throws Exception {
        view = new NightlyRateView();
    }

    @After
    public void tearDown() throws Exception {
        view = null;
    }

    private NightlyRateView view;

    @Test
    public void test01() {
        final Booking booking1 = new Booking("id1", new Guest("g1"), new Room("1"), new BookingOrigin("airbnb"),
                LocalDate
                        .of(2000, 4, 4), LocalDate.of(2000, 4, 6));
        final Booking booking2 = new Booking("id2", new Guest("g2"), new Room("2"), new BookingOrigin("airbnb"),
                LocalDate
                        .of(2000, 4, 4), LocalDate.of(2000, 4, 6));
        booking1.setGrossEarningsExpression("100");
        booking2.setGrossEarningsExpression("200");
        final List<BookingEntry> entries1 = booking1.getEntries();
        final List<BookingEntry> entries2 = booking2.getEntries();
        //System.out.println(entries.stream().map(b -> b.toString()).collect(Collectors.joining("\n")));
        view.addAll(entries1);
        view.addAll(entries2);
        assertThat(view.data.size(), is(1));


    }

}
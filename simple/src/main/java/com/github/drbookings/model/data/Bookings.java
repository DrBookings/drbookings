package com.github.drbookings.model.data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.github.drbookings.model.IBooking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.BookingEntry;

public class Bookings {



    public static long countNights(Collection<? extends Booking> bookings){
        return bookings.stream().mapToLong(b -> b.getNumberOfNights()).sum();
    }


}

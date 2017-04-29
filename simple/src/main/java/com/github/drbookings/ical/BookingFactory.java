package com.github.drbookings.ical;

import java.util.Collection;

import com.github.drbookings.model.ser.BookingBeanSer;

public interface BookingFactory {

    Collection<BookingBeanSer> build() throws Exception;

}

package com.github.drbookings;

import java.util.Collection;

import com.github.drbookings.model.ser.BookingBeanSer;

/**
 * @version $id$
 * @author alex
 *
 */
public interface BookingFactory {

	Collection<BookingBeanSer> build() throws Exception;

}

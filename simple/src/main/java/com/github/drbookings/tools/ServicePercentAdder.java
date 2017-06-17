package com.github.drbookings.tools;

import java.io.File;

import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;

public class ServicePercentAdder {

	public static void main(final String[] args) throws Exception {

		final File file = new File("/home/alex/bookings.xml");

		final DataStore ds = new XMLStorage().load(file);
		for (final BookingBeanSer bs : ds.getBookingsSer()) {
			if (bs.source.equalsIgnoreCase("booking")) {
				bs.serviceFeePercent = 0.12f;
			}
		}
		new XMLStorage().save(ds, file);
	}
}

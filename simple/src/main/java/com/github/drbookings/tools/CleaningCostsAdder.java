package com.github.drbookings.tools;

import java.io.File;

import com.github.drbookings.model.ser.CleaningBeanSer;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;

public class CleaningCostsAdder {

	public static void main(final String[] args) throws Exception {

		final File file = new File("/home/alex/bookings.xml");
		final float cleaningCosts = 30;

		final DataStore ds = new XMLStorage().load(file);
		for (final CleaningBeanSer bs : ds.getCleaningsSer()) {
			if (bs.name.equalsIgnoreCase("Natalia")) {
				bs.cleaningCosts = cleaningCosts;
			}
		}
		new XMLStorage().save(ds, file);
	}
}

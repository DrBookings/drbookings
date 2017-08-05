package com.github.drbookings.tools;

import java.io.File;
import java.util.Iterator;

import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.model.ser.CleaningBeanSer;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.XMLStorage;
import com.github.ktools1000.RandomString;
import com.github.ktools1000.io.BackupCreator;

public class Anonymiser {

	public static void main(final String[] args) throws Exception {

		final File file = new File("/home/alex/bookings-anonym.xml");
		new BackupCreator().makeBackup(file);

		final DataStore ds = new XMLStorage().load(file);
		for (final Iterator<BookingBeanSer> it = ds.getBookingsSer().iterator(); it.hasNext();) {
			final BookingBeanSer bs = it.next();
			bs.guestName = new RandomString().ofLength(bs.guestName.length()).toString();
		}
		for (final Iterator<CleaningBeanSer> it = ds.getCleaningsSer().iterator(); it.hasNext();) {
			final CleaningBeanSer bs = it.next();
			bs.name = new RandomString().ofLength(bs.name.length()).toString();
		}
		new XMLStorage().save(ds, file);

	}
}

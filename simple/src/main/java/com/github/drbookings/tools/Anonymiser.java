package com.github.drbookings.tools;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

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

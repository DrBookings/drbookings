/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
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
 */
package com.github.drbookings;

import java.time.LocalDate;

public class RoomEntry extends DateEntryImpl<Room> {

	private CleaningEntry cleaning;

	public RoomEntry(final LocalDate date, final Room element) {

		super(date, element);
	}

	public CleaningEntry getCleaning() {

		return cleaning;
	}

	public void setCleaning(final CleaningEntry cleaning) {

		if(getDate().equals(cleaning.getDate())) {
			this.cleaning = cleaning;
		}
		throw new IllegalArgumentException("Dates do not match");
	}
}

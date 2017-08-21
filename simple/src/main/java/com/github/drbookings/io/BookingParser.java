package com.github.drbookings.io;

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

import java.io.IOException;
import java.time.LocalDate;

import biweekly.component.VEvent;

public interface BookingParser {

    String getRoomName(VEvent e) throws IOException;

    String getGuestName(VEvent e) throws IOException;

    LocalDate getCheckInDate(VEvent e) throws IOException;

    LocalDate getCheckOutDate(VEvent e) throws IOException;

    String getExternalID(VEvent e) throws IOException;

}

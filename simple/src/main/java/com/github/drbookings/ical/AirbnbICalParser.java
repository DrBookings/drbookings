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

package com.github.drbookings.ical;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import com.github.drbookings.io.BookingParser;

import biweekly.component.VEvent;

public class AirbnbICalParser implements BookingParser {

    private final Map<String, String> vendorNameToNameMap;

    public AirbnbICalParser(final Map<String, String> vendorNameToNameMap) {
	this.vendorNameToNameMap = vendorNameToNameMap;
    }

    @Override
    public LocalDate getCheckInDate(final VEvent e) throws IOException {
	return e.getDateStart().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public LocalDate getCheckOutDate(final VEvent e) throws IOException {
	return e.getDateEnd().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public String getExternalID(final VEvent e) throws IOException {
	final String value = e.getSummary().getValue().trim();
	// final String name = value.substring(0, value.indexOf("(") -
	// 1).trim();
	final String externalID = value.substring(value.indexOf("(") + 1, value.indexOf(")")).trim();
	return externalID;
    }

    @Override
    public String getGuestName(final VEvent e) throws IOException {
	final String value = e.getSummary().getValue().trim();
	final String name = value.substring(0, value.indexOf("(") - 1).trim();
	// final String externalID = value.substring(value.indexOf("(") + 1,
	// value.indexOf(")")).trim();
	return name;
    }

    @Override
    public String getRoomName(final VEvent e) throws IOException {
	if (e.getLocation() == null) {
	    return null;
	}
	final String airbnbRoomName = e.getLocation().getValue().trim();
	final String ourName = vendorNameToNameMap.get(airbnbRoomName);
	return ourName;

    }

}

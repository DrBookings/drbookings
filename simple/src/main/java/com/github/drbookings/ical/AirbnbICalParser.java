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
    public String getRoomName(final VEvent e) throws IOException {
	if (e.getLocation() == null) {
	    return null;
	}
	final String airbnbRoomName = e.getLocation().getValue().trim();
	final String ourName = vendorNameToNameMap.get(airbnbRoomName);
	return ourName;

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
    public String getExternalID(final VEvent e) throws IOException {
	final String value = e.getSummary().getValue().trim();
	// final String name = value.substring(0, value.indexOf("(") -
	// 1).trim();
	final String externalID = value.substring(value.indexOf("(") + 1, value.indexOf(")")).trim();
	return externalID;
    }

    @Override
    public LocalDate getCheckInDate(final VEvent e) throws IOException {
	return e.getDateStart().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public LocalDate getCheckOutDate(final VEvent e) throws IOException {
	return e.getDateEnd().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

}

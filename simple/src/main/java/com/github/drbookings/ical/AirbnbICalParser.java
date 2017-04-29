package com.github.drbookings.ical;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;

import biweekly.component.VEvent;

public class AirbnbICalParser implements ICalParser {

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
	return e.getSummary().getValue().trim();
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

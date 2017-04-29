package com.github.drbookings.ical;

import java.io.IOException;
import java.time.LocalDate;

import biweekly.component.VEvent;

public interface ICalParser {

    String getRoomName(VEvent e) throws IOException;

    String getGuestName(VEvent e) throws IOException;

    LocalDate getCheckInDate(VEvent e) throws IOException;

    LocalDate getCheckOutDate(VEvent e) throws IOException;

}

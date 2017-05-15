package com.github.drbookings.io;

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

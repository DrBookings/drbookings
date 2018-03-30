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

package com.github.drbookings.google;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

public class EventFactory {

    public Event newEvent(final String summary, final LocalDate date, final String description) {
	final Event event = new Event();
	event.setSummary(summary);
	event.setDescription("Dr.Bookings:\n" + description);
	final Date startDate = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
	final Date endDate = new Date(startDate.getTime() + 86400000); // An
								       // all-day
								       // event
								       // is 1
								       // day
								       // (or
								       // 86400000
								       // ms)
								       // long

	final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	final String startDateStr = dateFormat.format(startDate);
	final String endDateStr = dateFormat.format(endDate);
	// Out of the 6 methods for creating a DateTime object with no time
	// element, only the String version works
	final DateTime startDateTime = new DateTime(startDateStr);
	final DateTime endDateTime = new DateTime(endDateStr);
	// Must use the setDate() method for an all-day event (setDateTime() is
	// used for timed events)
	final EventDateTime startEventDateTime = new EventDateTime().setDate(startDateTime);
	final EventDateTime endEventDateTime = new EventDateTime().setDate(endDateTime);
	event.setStart(startEventDateTime);
	event.setEnd(endEventDateTime);
	return event;
    }

}

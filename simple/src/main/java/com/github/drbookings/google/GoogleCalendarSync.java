/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.CleaningEntry;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar.Events.Delete;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import net.sf.kerner.utils.time.DateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class GoogleCalendarSync {

    public static final int DEFAULT_DAYS_AHEAD = 10;
    public static final int DEFAULT_DAYS_BEHIND = 3;
    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarSync.class);
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String APPLICATION_NAME = "drbookings";
    /**
     * Directory to store user credentials.
     */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
            ".store/calendar_sample");
    /**
     * Global instance of the HTTP transport.
     */
    private static HttpTransport httpTransport;
    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;
    private static com.google.api.services.calendar.Calendar client;
    private final MainManager manager;
    private int daysAhead = DEFAULT_DAYS_AHEAD;
    private int daysBehind = DEFAULT_DAYS_BEHIND;

    public GoogleCalendarSync(final MainManager manager) {
        this.manager = manager;

    }

    private static void addCheckInEvent(final BookingBean b) throws IOException {
        final CalendarListEntry flats = getCalendar();
        String note = "Checkin: ";
        if (b.getCheckInNote() != null) {
            note += b.getCheckInNote();
        }
        if (b.getSpecialRequestNote() != null) {
            note = note + "\n" + b.getSpecialRequestNote();
        }
        final Event checkInEvent = new EventFactory().newEvent(getCheckInSummary(b), b.getCheckIn(),
                b.getGuest().getName() + ": " + note);
        b.addCalendarId(addEvent(flats.getId(), checkInEvent));
    }

    private static void addCheckOutEvent(final BookingBean b) throws IOException {
        final CalendarListEntry flats = getCalendar();
        String note = "Checkout: ";
        if (b.getCheckOutNote() != null) {
            note += b.getCheckOutNote();
        }
        final Event event = new EventFactory().newEvent(getCheckOutSummary(b), b.getCheckOut(),
                b.getGuest().getName() + ": " + note);
        b.addCalendarId(addEvent(flats.getId(), event));

    }

    private static void addCleaningEvent(final CleaningEntry c) throws IOException {
        final CalendarListEntry flats = getCalendar();
        final String prefix = SettingsManager.getInstance().getRoomNamePrefix();
        final Event event = new EventFactory().newEvent(
                "Cleaning " + prefix + c.getRoom().getName() + " " + c.getElement().getName(), c.getDate(),
                "Cleaning event");
        c.addCalendarId(addEvent(flats.getId(), event));
    }

    private static String addEvent(final String calendarId, final Event event) throws IOException {
        final Event i = client.events().insert(calendarId, event).execute();
        if (logger.isInfoEnabled()) {
            logger.info("Created event: " + event.getSummary());
        }
        return i.getId();
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    private static Credential authorize() throws Exception {
        // load client secrets
        final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                new InputStreamReader(GoogleCalendarSync.class.getResourceAsStream("/client_secrets.json")));
        if (clientSecrets.getDetails().getClientId().startsWith("Enter")
                || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
            if (logger.isErrorEnabled()) {
                logger.error("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
                        + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
            }
            return null;
        }
        // set up authorization code flow
        final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
                clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
                .build();
        // authorize
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver())
                .authorize("557837674207-61uehop5b0u5enflhc7ata9a75sf731e.apps.googleusercontent.com");
    }

    private static void clearEvent(final String calendarId, final BookingBean b) throws IOException {
        for (final Iterator<String> it = b.getCalendarIds().iterator(); it.hasNext(); ) {
            final String id = it.next();
            final Delete d = client.events().delete(calendarId, id);
            if (logger.isDebugEnabled()) {
                logger.debug("Deleting " + d.getEventId());
            }
            try {
                d.execute();
            } catch (final GoogleJsonResponseException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.toString());
                }
            }
            it.remove();
        }
    }

    private static void clearEvent(final String calendarId, final Event event) throws IOException {
        if (isDrBookingEvent(event)) {
            if (logger.isInfoEnabled()) {
                logger.info("Deleting " + event.getSummary());
            }
            final Delete d = client.events().delete(calendarId, event.getId());
            d.execute();
        }
    }

    private static CalendarListEntry getCalendar() throws IOException {
        final CalendarList feed = client.calendarList().list().execute();
        for (final CalendarListEntry c : feed.getItems()) {
            if (c.getSummary().equalsIgnoreCase("flats")) {
                return c;
            }
        }
        return null;
    }

    private static String getCheckInSummary(final BookingBean b) {
        return getEventSummary("Check-in ", b);
    }

    private static String getCheckOutSummary(final BookingBean b) {
        return getEventSummary("Check-out ", b);
    }

    private static String getEventSummary(final String prefix, final BookingBean b) {
        return prefix + SettingsManager.getInstance().getRoomNamePrefix() + b.getRoom().getName() + " "
                + b.getBookingOrigin().getName();
    }

    private static boolean isDrBookingEvent(final Event event) {
        return event.getDescription() != null && (event.getDescription().startsWith("Dr.Bookings")
                || event.getDescription().startsWith("drbookings"));
    }

    public GoogleCalendarSync clear() throws IOException {
        return clear(LocalDate.now().minusDays(getDaysBehind()));
    }

    public GoogleCalendarSync clear(final LocalDate date) throws IOException {
        final CalendarListEntry flats = getCalendar();
        // Iterate over the events in the specified calendar
        String pageToken = null;
        int cnt = 0;
        do {
            final Events events;
            if (date != null) {
                events = client.events().list(flats.getId()).setTimeMin(new DateTime(new DateConverter().convert(date)))
                        .setPageToken(pageToken).execute();
            } else {
                events = client.events().list(flats.getId()).setPageToken(pageToken).execute();
            }
            final List<Event> items = events.getItems();
            for (final Event event : items) {
                if (event == null) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Skipping null event");
                    }
                    continue;
                }
                clearEvent(flats.getId(), event);
                cnt++;
            }
            pageToken = events.getNextPageToken();
        } while (pageToken != null);
        if (logger.isDebugEnabled()) {
            logger.debug("Processed " + cnt + " events");
        }

        return this;
    }

    public GoogleCalendarSync clearAll() throws IOException {
        return clear(null);
    }

    public int getDaysAhead() {
        return daysAhead;
    }

    public GoogleCalendarSync setDaysAhead(final int daysAhead) {
        this.daysAhead = daysAhead;
        return this;
    }

    public int getDaysBehind() {
        return daysBehind;
    }

    public GoogleCalendarSync setDaysBehind(final int daysBehind) {
        this.daysBehind = daysBehind;
        return this;
    }

    public GoogleCalendarSync init() throws Exception {
        // initialize the transport
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        // initialize the data store factory
        dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

        // authorization
        final Credential credential = authorize();

        // set up global Calendar instance
        client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME).build();

        return this;
    }

    public void write() throws IOException {
        writeBookings();
        writeCleanings();
        if (logger.isDebugEnabled()) {
            logger.debug("All done");
        }
    }

    private void writeBookings() throws IOException {
        for (final BookingBean b : manager.getBookings()) {
            if (b.getCheckIn().isAfter(LocalDate.now().minusDays(getDaysBehind()))
                    && b.getCheckIn().isBefore(LocalDate.now().plusDays(getDaysAhead()))) {
                addCheckInEvent(b);
            }
            if (b.getCheckOut().isAfter(LocalDate.now().minusDays(getDaysBehind()))
                    && b.getCheckOut().isBefore(LocalDate.now().plusDays(getDaysAhead()))) {
                addCheckOutEvent(b);
            }
        }
    }

    private void writeCleanings() throws IOException {
        for (final CleaningEntry c : manager.getCleaningEntries()) {
            if (c.getDate().isAfter(LocalDate.now().minusDays(getDaysBehind()))
                    && c.getDate().isBefore(LocalDate.now().plusDays(getDaysAhead()))) {
                addCleaningEvent(c);
            }
        }

    }
}

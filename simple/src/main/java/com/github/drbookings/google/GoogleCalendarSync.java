package com.github.drbookings.google;

import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar.Events.Delete;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

public class GoogleCalendarSync {

    private static final Logger logger = LoggerFactory.getLogger(GoogleCalendarSync.class);

    /** Global instance of the HTTP transport. */
    private static HttpTransport httpTransport;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    /**
     * Global instance of the {@link DataStoreFactory}. The best practice is to
     * make it a single globally shared instance across your application.
     */
    private static FileDataStoreFactory dataStoreFactory;

    /** Authorizes the installed application to access user's protected data. */
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

    private static final String APPLICATION_NAME = "drbookings";

    private static com.google.api.services.calendar.Calendar client;

    public static final int DEFAULT_DAYS_AHEAD = 10;

    private int daysAhead = DEFAULT_DAYS_AHEAD;

    private final MainManager manager;

    /** Directory to store user credentials. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
	    ".store/calendar_sample");

    public GoogleCalendarSync(final MainManager manager) {
	this.manager = manager;

    }

    public int getDaysAhead() {
	return daysAhead;
    }

    public void setDaysAhead(final int daysAhead) {
	this.daysAhead = daysAhead;
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

    private static CalendarListEntry getCalendar() throws IOException {
	final CalendarList feed = client.calendarList().list().execute();
	for (final CalendarListEntry c : feed.getItems()) {
	    if (c.getSummary().equalsIgnoreCase("flats")) {
		return c;
	    }
	}
	return null;
    }

    public GoogleCalendarSync clear() throws IOException {
	final CalendarListEntry flats = getCalendar();
	// final Calendar cal = Calendar.getInstance();
	// cal.setTime(new Date());
	// cal.add(Calendar.MONTH, -3);

	// Iterate over the events in the specified calendar
	String pageToken = null;
	int cnt = 0;
	do {
	    final Events events = client.events().list(flats.getId()).setPageToken(pageToken).execute();
	    final List<Event> items = events.getItems();
	    for (final Event event : items) {
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

    private void clearEvent(final String calendarId, final Event event) throws IOException {
	if (event.getDescription() != null && (event.getDescription().startsWith("Dr.Bookings")
		|| event.getDescription().startsWith("drbookings"))) {
	    final Delete d = client.events().delete(calendarId, event.getId());
	    if (logger.isInfoEnabled()) {
		logger.info("Deleting " + event.getSummary());
	    }
	    d.execute();
	}
    }

    public void write() throws IOException {
	for (final Booking b : manager.getBookings()) {
	    if (b.getCheckIn().isAfter(LocalDate.now().minusDays(1))
		    && b.getCheckIn().isBefore(LocalDate.now().plusDays(getDaysAhead()))) {
		addCheckInEvent(b);
	    }
	    if (b.getCheckOut().isAfter(LocalDate.now().minusDays(1))
		    && b.getCheckOut().isBefore(LocalDate.now().plusDays(getDaysAhead()))) {
		addCheckOutEvent(b);
	    }
	}
    }

    private static void addCheckInEvent(final Booking b) throws IOException {
	final CalendarListEntry flats = getCalendar();
	String checkInNote = "n/a";
	if (b.getCheckInNote() != null) {
	    checkInNote = b.getCheckInNote();
	}
	if (b.getSpecialRequestNote() != null) {
	    checkInNote = checkInNote + "\n" + b.getSpecialRequestNote();
	}
	Event checkInEvent = new EventFactory().newEvent(getCheckInSummary(b), b.getCheckIn(),
		b.getGuest().getName() + ": " + checkInNote);
	checkInEvent = client.events().insert(flats.getId(), checkInEvent).execute();
	if (logger.isInfoEnabled()) {
	    logger.info("Created event: " + checkInEvent.getSummary());
	}

    }

    private static void addCheckOutEvent(final Booking b) throws IOException {
	final CalendarListEntry flats = getCalendar();
	String checkInNote = "n/a";
	if (b.getCheckInNote() != null) {
	    checkInNote = b.getCheckOutNote();
	}
	Event checkInEvent = new EventFactory().newEvent(getCheckOutSummary(b), b.getCheckOut(),
		b.getGuest().getName() + ": " + checkInNote);
	checkInEvent = client.events().insert(flats.getId(), checkInEvent).execute();
	if (logger.isInfoEnabled()) {
	    logger.info("Created event: " + checkInEvent.getSummary());
	}

    }

    private static String getCheckInSummary(final Booking b) {
	return "Check-in " + SettingsManager.getInstance().getRoomNamePrefix() + b.getRoom().getName() + " "
		+ b.getBookingOrigin().getName();
    }

    private static String getCheckOutSummary(final Booking b) {
	return "Check-out " + SettingsManager.getInstance().getRoomNamePrefix() + b.getRoom().getName() + " "
		+ b.getBookingOrigin().getName();
    }

}

package com.github.drbookings.ui.io.googlecalendar.runnables;

import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.core.api.BookingManager;
import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.impl.BookingBean;
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
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import net.sf.kerner.utils.rcp.RunnableProto;

public class RunnableImportGoogleCalendar extends RunnableProto<List<Booking>> {

	/** Directory to store user credentials. */
	private static final File DATA_STORE_DIR = new File(System.getProperty("user.home"), ".store/calendar_sample");

	/**
	 * Global instance of the {@link DataStoreFactory}. The best practice is to
	 * make it a single globally shared instance across your application.
	 */
	private static FileDataStoreFactory dataStoreFactory;

	/** Global instance of the HTTP transport. */
	private static HttpTransport httpTransport;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private final static Logger logger = LoggerFactory.getLogger(RunnableImportGoogleCalendar.class);

	/** Authorizes the installed application to access user's protected data. */
	private static Credential authorize() throws Exception {
		// load client secrets
		final GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
				new InputStreamReader(RunnableImportGoogleCalendar.class.getResourceAsStream("/client_secrets.json")));
		if (clientSecrets.getDetails().getClientId().startsWith("Enter")
				|| clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
			System.out.println("Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
					+ "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
			System.exit(1);
		}
		// set up authorization code flow
		final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
						.build();
		// authorize
		return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
	}

	private final BookingManager manager;

	private final String url;

	public RunnableImportGoogleCalendar(final BookingManager manager, final String url) {
		this.manager = manager;
		this.url = url;
	}

	@Override
	protected void onSuccess(final List<Booking> result) {

		super.onSuccess(result);
		manager.addAllBookings(result);

	}

	@Override
	protected List<Booking> process(final IProgressMonitor monitor) throws Exception {

		try {
			if (logger.isInfoEnabled()) {
				logger.info("Adding from " + url);
			}
			// initialize the transport
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			// initialize the data store factory
			dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

			// authorization
			final Credential credential = authorize();

			// set up global Calendar instance
			final Calendar client = new com.google.api.services.calendar.Calendar.Builder(httpTransport, JSON_FACTORY,
					credential).setApplicationName("drbookings").build();

			final CalendarList list = client.calendarList().list().execute();

			String id = null;

			for (final CalendarListEntry item : list.getItems()) {

				if (item.getSummary().contains("airbnb")) {
					id = item.getId();
				}
			}

			final Events feed = client.events().list(id).execute();

			final List<Booking> result = new ArrayList<>();

			for (final Event item : feed.getItems()) {
				System.out.println(item);
				result.add(new BookingBean(item.getSummary(), LocalDate.parse(item.getStart().getDate().toString()),
						LocalDate.parse(item.getEnd().getDate().toString())));
			}

			return result;

		} finally {
			monitor.done();
		}

	}

}

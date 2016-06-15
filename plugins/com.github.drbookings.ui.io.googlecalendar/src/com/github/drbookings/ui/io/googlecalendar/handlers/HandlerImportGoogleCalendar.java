package com.github.drbookings.ui.io.googlecalendar.handlers;

import javax.inject.Inject;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.core.api.BookingManager;
import com.github.drbookings.ui.io.googlecalendar.runnables.RunnableImportGoogleCalendar;

public class HandlerImportGoogleCalendar {

	private final static Logger logger = LoggerFactory.getLogger(HandlerImportGoogleCalendar.class);

	@Inject
	private BookingManager manager;

	@Execute
	public void execute(final Shell shell) {

		final RunnableImportGoogleCalendar runnable = new RunnableImportGoogleCalendar(manager, "");
		final ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		try {
			monitor.run(true, true, runnable);
		} catch (final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
}

package com.github.drbookings.handlers;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.runnables.RunnableImportCSVBooking;

public class HandlerImportXLSBooking {

	private final static Logger logger = LoggerFactory.getLogger(HandlerImportXLSBooking.class);

	@Execute
	public void execute(final Shell shell) {

		final FileDialog dialog = new FileDialog(shell);
		final String file = dialog.open();
		if (logger.isInfoEnabled()) {
			logger.info("Reading " + file);
		}
		final RunnableImportCSVBooking runnable = new RunnableImportCSVBooking(new File(file));
		final ProgressMonitorDialog monitor = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
		try {
			monitor.run(true, true, runnable);
		} catch (final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
}

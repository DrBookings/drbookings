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

package com.github.drbookings.ui;

import com.github.drbookings.ui.controller.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Labeled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public abstract class AbstractDrBookingsService<T> extends Service<T> {

    private final static Logger logger = LoggerFactory.getLogger(AbstractDrBookingsService.class);

    private final Labeled labeled;

    public AbstractDrBookingsService(final Labeled labeled) {
	this.labeled = labeled;

	addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> {
	    final Throwable e = getException();
	    if (logger.isErrorEnabled())
		logger.error(e.toString());
	    labeled.setText("Error: " + e.getLocalizedMessage());
	});

	addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, event -> {
	    labeled.setText(null);
	});

	addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, event -> {
	    labeled.setText("Working..");
	});

	addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
	    labeled.textProperty().unbind();
	    labeled.setText(null);

	});

	setExecutor(MainController.EXECUTOR);
    }
}

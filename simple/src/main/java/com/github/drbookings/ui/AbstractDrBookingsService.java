package com.github.drbookings.ui;

import com.github.drbookings.ui.controller.MainController;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.scene.control.Labeled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDrBookingsService<T> extends Service<T> {

    private final static Logger logger = LoggerFactory.getLogger(AbstractDrBookingsService.class);

    private final Labeled labeled;

    public AbstractDrBookingsService(final Labeled labeled) {
        this.labeled = labeled;

        addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> {
            final Throwable e = getException();
            if (logger.isErrorEnabled()) {
                logger.error(e.toString());
            }
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

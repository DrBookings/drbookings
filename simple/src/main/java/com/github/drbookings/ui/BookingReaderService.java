package com.github.drbookings.ui;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.BookingFactory;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.ser.BookingBeanSer;
import com.github.drbookings.ser.DataStore;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class BookingReaderService extends Service<Collection<BookingBeanSer>> {

    private final static Logger logger = LoggerFactory.getLogger(BookingReaderService.class);

    private final BookingFactory factory;

    public BookingReaderService(final MainManager manager, final BookingFactory factory) {
	super();
	this.factory = factory;
	setOnFailed(ee -> {
	    final Throwable e = getException();
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	    UIUtils.showError(e);
	});
	setOnSucceeded(e -> {
	    if (logger.isDebugEnabled()) {
		logger.debug("read " + getValue().size() + " bookings");
		try {
		    new DataStore().setBookingSer(getValue()).load(manager);
		} catch (final Exception e1) {
		    if (logger.isErrorEnabled()) {
			logger.error(e1.getLocalizedMessage(), e);
		    }
		    UIUtils.showError(e1);
		}
	    }
	});
    }

    @Override
    protected Task<Collection<BookingBeanSer>> createTask() {
	return new Task<Collection<BookingBeanSer>>() {

	    @Override
	    protected Collection<BookingBeanSer> call() throws Exception {
		return factory.build();
	    }
	};
    }

}

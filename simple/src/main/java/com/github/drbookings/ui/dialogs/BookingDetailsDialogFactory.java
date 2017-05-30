package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.controller.BookingDetailsController;

public class BookingDetailsDialogFactory extends AbstractDialogFactory implements DialogFactory {

    private static final Logger logger = LoggerFactory.getLogger(BookingDetailsDialogFactory.class);
    private final MainManager manager;

    public BookingDetailsDialogFactory(final MainManager manager) {
	this.manager = manager;
	setFxml("/fxml/BookingDetailsView.fxml");
	setTitle("Booking Details");
	setHeight(600);
	setWidth(500);
    }

    @Override
    protected void visitController(final Object controller) {
	final BookingDetailsController c = (BookingDetailsController) controller;
	c.setManager(manager);
    }
}

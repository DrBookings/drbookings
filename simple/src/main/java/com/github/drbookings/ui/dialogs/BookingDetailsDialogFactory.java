package com.github.drbookings.ui.dialogs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingDetailsDialogFactory extends AbstractDialogFactory implements DialogFactory {

    private static final Logger logger = LoggerFactory.getLogger(BookingDetailsDialogFactory.class);

    public BookingDetailsDialogFactory() {
	setFxml("/fxml/BookingDetailsView.fxml");
	setTitle("Booking Details");
    }
}

package com.github.drbookings.ui.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BookingDetailsDialogBuilder {

    private static final Logger logger = LoggerFactory.getLogger(BookingDetailsDialogBuilder.class);

    public static void doShowBookingDetails() {
	try {
	    final FXMLLoader loader = new FXMLLoader(
		    BookingDetailsDialogBuilder.class.getResource("/fxml/BookingDetailsView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root);
	    stage.setTitle("Booking Details");
	    stage.setScene(scene);
	    stage.setWidth(400);
	    stage.setHeight(600);
	    final BookingDetailsController c = loader.getController();
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }
}

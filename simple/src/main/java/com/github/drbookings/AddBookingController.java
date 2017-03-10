package com.github.drbookings;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BookingDates;
import com.github.drbookings.model.DataModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookingController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(AddBookingController.class);

    @FXML
    Button buttonOK;

    @FXML
    DatePicker datePickerCheckIn;

    @FXML
    DatePicker datePickerCheckOut;

    @FXML
    TextField textFieldSource;

    @FXML
    TextField textFieldRoom;

    @FXML
    TextField textFieldGuestName;

    @FXML
    void handleButtonOK(final ActionEvent event) {
	final BookingDates b = BookingDates.buildBookingDate(datePickerCheckIn.getValue(),
		datePickerCheckOut.getValue(), textFieldSource.getText().trim(), textFieldRoom.getText().trim(),
		textFieldGuestName.getText().trim());
	if (logger.isDebugEnabled()) {
	    logger.debug("Adding booking ");
	}
	try {
	    DataModel.getInstance().add(b);
	} catch (final OverbookingException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
	final Stage stage = (Stage) buttonOK.getScene().getWindow();
	stage.close();
    }

    @FXML
    void handleButtonSetCheckInDate(final ActionEvent event) {
	final LocalDate date = datePickerCheckIn.getValue();
	if (logger.isDebugEnabled()) {
	    logger.debug("Selected Check-in " + date);
	}
    }

    @FXML
    void handleButtonSetCheckOutDate(final ActionEvent event) {
	final LocalDate date = datePickerCheckOut.getValue();
	if (logger.isDebugEnabled()) {
	    logger.debug("Selected Check-out " + date);
	}
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

}

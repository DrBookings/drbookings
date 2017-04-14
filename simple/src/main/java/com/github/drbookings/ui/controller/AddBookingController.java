package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.manager.MainManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookingController implements Initializable {

    public MainManager getManager() {
	return manager;
    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
    }

    private final static Logger logger = LoggerFactory.getLogger(AddBookingController.class);

    private MainManager manager;

    @FXML
    Button buttonOK;

    @FXML
    DatePicker datePickerCheckIn;

    @FXML
    DatePicker datePickerCheckOut;

    @FXML
    TextField textFieldSource;

    @FXML
    ComboBox<String> comboBoxRoom;

    @FXML
    TextField textFieldGuestName;

    @FXML
    void handleButtonOK(final ActionEvent event) {
	final boolean valid = validateInput();
	if (valid) {
	    try {

		getManager().addBooking(datePickerCheckIn.getValue(), datePickerCheckOut.getValue(),
			textFieldGuestName.getText().trim(), comboBoxRoom.getSelectionModel().getSelectedItem(),
			textFieldSource.getText().trim());

	    } catch (final OverbookingException e) {
		if (logger.isDebugEnabled()) {
		    logger.debug(e.getLocalizedMessage());
		}
		UIUtils.showError("Overbooking", e.getLocalizedMessage());
	    }
	    final Stage stage = (Stage) buttonOK.getScene().getWindow();
	    stage.close();
	}

    }

    @FXML
    void handleButtonSetCheckInDate(final ActionEvent event) {
	final LocalDate date = datePickerCheckIn.getValue();
	// if (logger.isDebugEnabled()) {
	// logger.debug("Selected Check-in " + date);
	// }
	if (datePickerCheckOut.getValue() == null) {
	    datePickerCheckOut.setValue(date.plusDays(2));
	}
    }

    @FXML
    void handleButtonSetCheckOutDate(final ActionEvent event) {
	final LocalDate date = datePickerCheckOut.getValue();
	if (datePickerCheckIn.getValue() == null) {
	    datePickerCheckIn.setValue(date.minusDays(2));
	}
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	comboBoxRoom.getItems().addAll("1", "2", "3", "4");
    }

    private boolean validateInput() {
	if (datePickerCheckIn.getValue() == null) {
	    UIUtils.showError("Invalid Input", "Please select a check-in date");
	    return false;
	}
	if (datePickerCheckOut.getValue() == null) {
	    UIUtils.showError("Invalid Input", "Please select a check-out date");
	    return false;
	}
	if (datePickerCheckOut.getValue().isBefore(datePickerCheckIn.getValue())) {
	    UIUtils.showError("Invalid Input", "Please choose a check-out date that is after check-in");
	    return false;
	}
	if (textFieldGuestName.getText().trim().isEmpty()) {
	    UIUtils.showError("Invalid Input", "Please choose a guest name");
	    return false;
	}
	if (comboBoxRoom.getSelectionModel().getSelectedItem() == null
		|| comboBoxRoom.getSelectionModel().getSelectedItem().isEmpty()) {
	    UIUtils.showError("Invalid Input", "Please choose a room");
	    return false;
	}
	return true;
    }

}

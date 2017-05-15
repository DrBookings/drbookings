package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.LocalDates;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.UIUtils;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
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
    private Button buttonOK;

    @FXML
    DatePicker datePickerCheckIn;

    @FXML
    DatePicker datePickerCheckOut;

    @FXML
    private TextField textFieldSource;

    @FXML
    private TextField textFieldGrossEarnings;

    @FXML
    ComboBox<String> comboBoxRoom;

    @FXML
    private TextField textFieldGuestName;

    @FXML
    private Label infoLabel;

    @FXML
    void handleButtonOK(final ActionEvent event) {
	final boolean valid = validateInput();
	if (valid) {
	    try {
		final Booking b = getManager().createBooking(datePickerCheckIn.getValue(),
			datePickerCheckOut.getValue(), textFieldGuestName.getText().trim(),
			comboBoxRoom.getSelectionModel().getSelectedItem(), textFieldSource.getText().trim());
		b.setGrossEarningsExpression(getGrossEarnings() + "");
		getManager().addBooking(b);

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

    private double getGrossEarnings() {
	if (textFieldGrossEarnings.getText() != null) {
	    try {
		return Double.parseDouble(textFieldGrossEarnings.getText());
	    } catch (final NumberFormatException e) {

	    }
	}
	return 0;
    }

    @FXML
    void handleButtonSetCheckInDate(final ActionEvent event) {
	final LocalDate date = datePickerCheckIn.getValue();
	// if (logger.isDebugEnabled()) {
	// logger.debug("Selected Check-in " + date);
	// }
	if (datePickerCheckOut.getValue() == null) {
	    datePickerCheckOut.setValue(date.plusDays(3));
	}

	updateInfoLabel();
    }

    private void updateInfoLabel() {
	if (datePickerCheckIn.getValue() == null) {
	    return;
	}
	if (datePickerCheckOut.getValue() == null) {
	    return;
	}

	final double grossEarnings = getGrossEarnings();

	final long numberOfNights = LocalDates.getNumberOfNights(datePickerCheckIn.getValue(),
		datePickerCheckOut.getValue());
	if (grossEarnings > 0) {
	    infoLabel.setText(numberOfNights + " nights. " + String.format("%6.2f", grossEarnings / numberOfNights)
		    + " per night.");
	} else {
	    infoLabel.setText(numberOfNights + " nights.");
	}

    }

    @FXML
    void handleButtonSetCheckOutDate(final ActionEvent event) {
	final LocalDate date = datePickerCheckOut.getValue();
	if (datePickerCheckIn.getValue() == null) {
	    datePickerCheckIn.setValue(date.minusDays(3));
	}

	updateInfoLabel();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	comboBoxRoom.getItems().addAll("1", "2", "3", "4");
	textFieldGrossEarnings.textProperty()
		.addListener((ChangeListener<String>) (observable, oldValue, newValue) -> updateInfoLabel());
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

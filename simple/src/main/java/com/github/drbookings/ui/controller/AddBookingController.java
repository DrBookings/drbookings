package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.BookingDates;
import com.github.drbookings.model.DataModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookingController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(AddBookingController.class);

    static void showError(final String shortMsg, final String msg) {
	final Alert alert = new Alert(AlertType.ERROR);
	alert.setTitle("Error");
	alert.setHeaderText(shortMsg);
	final Label label = new Label(msg);
	label.setWrapText(true);
	alert.getDialogPane().setContent(label);
	alert.showAndWait();
    }

    private DataModel dataModel;

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

    public DataModel getDataModel() {
	return dataModel;
    }

    @FXML
    void handleButtonOK(final ActionEvent event) {
	final boolean valid = validateInput();
	if (valid) {
	    try {
		final BookingDates b = BookingDates.buildBookingDate(datePickerCheckIn.getValue(),
			datePickerCheckOut.getValue(), textFieldSource.getText().trim(),
			comboBoxRoom.getSelectionModel().getSelectedItem(), textFieldGuestName.getText().trim());
		if (logger.isDebugEnabled()) {
		    logger.debug("Adding booking ");
		}
		getDataModel().add(b);
	    } catch (final OverbookingException e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	    final Stage stage = (Stage) buttonOK.getScene().getWindow();
	    stage.close();
	}

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
	comboBoxRoom.getItems().addAll("1", "2", "3", "4");
    }

    public void setDataModel(final DataModel dataModel) {
	this.dataModel = dataModel;
    }

    private boolean validateInput() {
	if (datePickerCheckIn.getValue() == null) {
	    showError("Invalid Input", "Please select a check-in date");
	    return false;
	}
	if (datePickerCheckOut.getValue() == null) {
	    showError("Invalid Input", "Please select a check-out date");
	    return false;
	}
	if (datePickerCheckOut.getValue().isBefore(datePickerCheckIn.getValue())) {
	    showError("Invalid Input", "Please choose a check-out date that is after check-in");
	    return false;
	}
	if (textFieldGuestName.getText().trim().isEmpty()) {
	    showError("Invalid Input", "Please choose a guest name");
	    return false;
	}
	if (comboBoxRoom.getSelectionModel().getSelectedItem() == null
		|| comboBoxRoom.getSelectionModel().getSelectedItem().isEmpty()) {
	    showError("Invalid Input", "Please choose a room");
	    return false;
	}
	return true;
    }

}

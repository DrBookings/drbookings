package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DateRange;
import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.data.manager.BookingManager;
import com.github.drbookings.model.manager.DataModel;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddBookingController implements Initializable {

    public BookingManager getBookingManager() {
	return bookingManager;
    }

    public void setBookingManager(final BookingManager bookingManager) {
	this.bookingManager = bookingManager;
    }

    private final static Logger logger = LoggerFactory.getLogger(AddBookingController.class);

    private DataModel dataModel;

    private BookingManager bookingManager;

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
		final DateRange dateRange = new DateRange(datePickerCheckIn.getValue(), datePickerCheckOut.getValue());
		getBookingManager().addBooking(textFieldGuestName.getText().trim(),
			comboBoxRoom.getSelectionModel().getSelectedItem(), datePickerCheckIn.getValue(),
			datePickerCheckOut.getValue());
		for (final LocalDate date : dateRange) {
		    // if (logger.isDebugEnabled()) {
		    // logger.debug("Building booking for " + date);
		    // }
		    getDataModel().add(BookingBean
			    .create(textFieldGuestName.getText().trim(),
				    comboBoxRoom.getSelectionModel().getSelectedItem(), date)
			    .setSource(textFieldSource.getText().trim()));

		}

		// if (logger.isDebugEnabled()) {
		// logger.debug("Adding booking ");
		// }

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

    public void setDataModel(final DataModel dataModel) {
	this.dataModel = dataModel;
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

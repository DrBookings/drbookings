/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.ui.controller;

import com.github.drbookings.BookingBean;
import com.github.drbookings.LocalDates;
import com.github.drbookings.SettingsManager;
import com.github.drbookings.exception.OverbookingException;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.FXUIUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddBookingController implements Initializable {

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
    private TextField textFieldServiceFees;

    @FXML
    private TextField textFieldServiceFeesPercent;

    @FXML
    private Label infoLabel;

    private double getGrossEarnings() {
	if (textFieldGrossEarnings.getText() != null) {
	    try {
		return Double.parseDouble(textFieldGrossEarnings.getText());
	    } catch (final NumberFormatException e) {

	    }
	}
	return 0;
    }

    public MainManager getManager() {
	return manager;
    }

    @FXML
    void handleButtonOK(final ActionEvent event) {
	final boolean valid = validateInput();
	if (valid) {
	    try {
		final BookingBean b = getManager().createAndAddBooking(datePickerCheckIn.getValue(),
			datePickerCheckOut.getValue(), textFieldGuestName.getText().trim(),
			comboBoxRoom.getSelectionModel().getSelectedItem(), textFieldSource.getText().trim());
		b.setGrossEarningsExpression(getGrossEarnings() + "");
		b.setCleaningFees(SettingsManager.getInstance().getCleaningFees());
		try {
		    b.setServiceFeesPercent(Float.parseFloat(textFieldServiceFeesPercent.getText()));
		} catch (final NumberFormatException e) {
		    if (logger.isDebugEnabled()) {
			logger.debug(e.toString());
		    }
		}
		try {
		    b.setServiceFee(Float.parseFloat(textFieldServiceFees.getText()));
		} catch (final NumberFormatException e) {
		    if (logger.isDebugEnabled()) {
			logger.debug(e.toString());
		    }
		}

	    } catch (final OverbookingException e) {
		if (logger.isDebugEnabled()) {
		    logger.debug(e.getLocalizedMessage());
		}
		FXUIUtils.showError("Overbooking", e.getLocalizedMessage());
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
	    datePickerCheckOut.setValue(date.plusDays(3));
	}

	updateInfoLabel();
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
	final List<String> numbers = new ArrayList<>();
	for (int i = 1; i <= SettingsManager.getInstance().getNumberOfRooms(); i++) {
	    numbers.add("" + i);
	}
	comboBoxRoom.getItems().addAll(numbers);
	textFieldGrossEarnings.textProperty().addListener((observable, oldValue, newValue) -> updateInfoLabel());
	textFieldServiceFees.setText(String.format("%4.2f", SettingsManager.getInstance().getServiceFees()));
	textFieldServiceFeesPercent
		.setText(String.format("%4.2f", SettingsManager.getInstance().getServiceFeesPercent()));
	textFieldSource.textProperty().addListener(new ChangeListener<String>() {

	    @Override
	    public void changed(final ObservableValue<? extends String> observable, final String oldValue,
		    final String newValue) {
		if ("booking".equalsIgnoreCase(newValue)) {
		    textFieldServiceFeesPercent.setText("12");
		} else if ("airbnb".equalsIgnoreCase(newValue)) {
		    textFieldServiceFeesPercent.setText("0");
		}
	    }
	});
    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
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

    private boolean validateInput() {
	if (datePickerCheckIn.getValue() == null) {
	    FXUIUtils.showError("Invalid Input", "Please select a check-in date");
	    return false;
	}
	if (datePickerCheckOut.getValue() == null) {
	    FXUIUtils.showError("Invalid Input", "Please select a check-out date");
	    return false;
	}
	if (datePickerCheckOut.getValue().isBefore(datePickerCheckIn.getValue())) {
	    FXUIUtils.showError("Invalid Input", "Please choose a check-out date that is after check-in");
	    return false;
	}
	if (textFieldGuestName.getText().trim().isEmpty()) {
	    FXUIUtils.showError("Invalid Input", "Please choose a guest name");
	    return false;
	}
	if (comboBoxRoom.getSelectionModel().getSelectedItem() == null
		|| comboBoxRoom.getSelectionModel().getSelectedItem().isEmpty()) {
	    FXUIUtils.showError("Invalid Input", "Please choose a room");
	    return false;
	}
	return true;
    }

}

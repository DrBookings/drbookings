package com.github.drbookings.ui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

public class ModifyBookingController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ModifyBookingController.class);

    public MainManager getManager() {
	return manager;
    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
    }

    @FXML
    private DatePicker datePickerCheckIn;

    @FXML
    private DatePicker datePickerCheckOut;

    @FXML
    private Label summaryLabel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	CellSelectionManager.getInstance().getSelection().addListener(roomListener);
	update(CellSelectionManager.getInstance().getSelection());

    }

    private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> update(c.getList()));

    private Booking booking;

    private MainManager manager;

    private void update(final ObservableList<? extends RoomBean> rooms) {
	final List<Booking> bookings = new ArrayList<>(rooms.stream()
		.flatMap(r -> r.getBookingEntries().stream().map(b -> b.getElement())).collect(Collectors.toSet()));
	Collections.sort(bookings);
	if (!bookings.isEmpty()) {
	    update(bookings.get(0));
	} else {
	    clearAll();
	}
    }

    private void update(final Booking booking) {
	this.booking = booking;
	summaryLabel.setText(booking.getGuest().toString());
	datePickerCheckIn.setValue(booking.getCheckIn());
	datePickerCheckOut.setValue(booking.getCheckOut());
    }

    private void clearAll() {
	datePickerCheckIn.setValue(null);
	datePickerCheckOut.setValue(null);

    }

    @FXML
    void handleButtonSaveChanges(final ActionEvent event) {
	try {
	    manager.modifyBooking(booking, datePickerCheckIn.getValue(), datePickerCheckOut.getValue());
	} catch (final OverbookingException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
    }

}

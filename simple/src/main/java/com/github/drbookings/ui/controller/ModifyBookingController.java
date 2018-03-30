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

import com.github.drbookings.OverbookingException;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ModifyBookingController implements Initializable {

	private static final Logger logger = LoggerFactory.getLogger(ModifyBookingController.class);

	public MainManager getManager() {
		return manager;
	}

	public void setManager(final MainManager manager) {
		this.manager = manager;
	}

	@FXML
	private CheckBox splitBooking;

	@FXML
	private DatePicker datePickerCheckIn;

	@FXML
	private DatePicker datePickerCheckOut;

	@FXML
	private DatePicker dateOfPayment;

	@FXML
	private Label summaryLabel;

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		RoomBeanSelectionManager.getInstance().selectionProperty().addListener(roomListener);
		update(RoomBeanSelectionManager.getInstance().getSelection());
	}

	private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> update(c.getList()));

    private BookingBean booking;

	private MainManager manager;

	private void update(final List<? extends RoomBean> rooms) {
        final List<BookingBean> bookings = new ArrayList<>(rooms.stream()
				.flatMap(r -> r.getBookingEntries().stream().map(b -> b.getElement())).collect(Collectors.toSet()));
		Collections.sort(bookings);
		if (!bookings.isEmpty()) {
			update(bookings.get(0));
		} else {
			clearAll();
		}
	}

    private void update(final BookingBean booking) {

		this.booking = booking;
		System.err.println(booking.isSplitBooking());
		splitBooking.setSelected(booking.isSplitBooking());
		summaryLabel.setText(booking.getGuest().toString());
		datePickerCheckIn.setValue(booking.getCheckIn());
		datePickerCheckOut.setValue(booking.getCheckOut());
		dateOfPayment.setValue(booking.getDateOfPayment());
	}

	private void clearAll() {
		datePickerCheckIn.setValue(null);
		datePickerCheckOut.setValue(null);
		dateOfPayment.setValue(null);

	}

	@FXML
	void handleButtonSaveChanges(final ActionEvent event) {
		booking.setDateOfPayment(dateOfPayment.getValue());
		System.err.println(splitBooking.isSelected());
		booking.setSplitBooking(splitBooking.isSelected());
		try {
			manager.modifyBooking(booking, datePickerCheckIn.getValue(), datePickerCheckOut.getValue());
		} catch (final OverbookingException e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}
}

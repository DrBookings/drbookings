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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ICalSettingsController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(ICalSettingsController.class);

    @FXML
    private TextField vendorRoomName1;

    @FXML
    private TextField ourRoomName1;

    @FXML
    private TextField vendorRoomName2;

    @FXML
    private TextField ourRoomName2;

    @FXML
    private TextField vendorRoomName3;

    @FXML
    private TextField ourRoomName3;

    @FXML
    private TextField vendorRoomName4;

    @FXML
    private TextField ourRoomName4;

    @FXML
    private TextField vendorRoomName5;

    @FXML
    private TextField ourRoomName5;

    @FXML
    private TextField vendorRoomName6;

    @FXML
    private TextField ourRoomName6;

    @FXML
    private TextField vendorRoomName7;

    @FXML
    private TextField ourRoomName7;

    @FXML
    private TextField vendorRoomName8;

    @FXML
    private TextField ourRoomName8;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	try {
	    final Map<String, String> map = SettingsManager.getInstance().getRoomNameMappings();
	    final List<Map.Entry<String, String>> l = new ArrayList<>(map.entrySet());
	    TextField vendor = null;
	    TextField our = null;
	    for (int i = 0; i < l.size(); i++) {
		if (i == 0) {
		    vendor = vendorRoomName1;
		    our = ourRoomName1;
		} else if (i == 1) {
		    vendor = vendorRoomName2;
		    our = ourRoomName2;
		} else if (i == 2) {
		    vendor = vendorRoomName3;
		    our = ourRoomName3;
		} else if (i == 3) {
		    vendor = vendorRoomName4;
		    our = ourRoomName4;
		} else if (i == 4) {
		    vendor = vendorRoomName5;
		    our = ourRoomName5;
		} else if (i == 5) {
		    vendor = vendorRoomName6;
		    our = ourRoomName6;
		} else if (i == 6) {
		    vendor = vendorRoomName7;
		    our = ourRoomName7;
		} else if (i == 7) {
		    vendor = vendorRoomName8;
		    our = ourRoomName8;
		} else {
		    // no more mappings supported
		    break;
		}
		vendor.setText(l.get(i).getKey());
		our.setText(l.get(i).getValue());
	    }
	} catch (final Exception e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
    }

    @FXML
    private void handleSave(final ActionEvent event) {
	final Map<String, String> map = new LinkedHashMap<>();
	map.put(vendorRoomName1.getText().trim(), ourRoomName1.getText().trim());
	map.put(vendorRoomName2.getText().trim(), ourRoomName2.getText().trim());
	map.put(vendorRoomName3.getText().trim(), ourRoomName3.getText().trim());
	map.put(vendorRoomName4.getText().trim(), ourRoomName4.getText().trim());
	map.put(vendorRoomName5.getText().trim(), ourRoomName5.getText().trim());
	map.put(vendorRoomName6.getText().trim(), ourRoomName6.getText().trim());
	map.put(vendorRoomName7.getText().trim(), ourRoomName7.getText().trim());
	map.put(vendorRoomName8.getText().trim(), ourRoomName8.getText().trim());
	try {
	    SettingsManager.getInstance().setRoomNameMapping(map);
	    ((Stage) vendorRoomName1.getScene().getWindow()).close();
	} catch (final IOException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}

    }
}

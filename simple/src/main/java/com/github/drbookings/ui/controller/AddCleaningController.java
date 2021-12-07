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

import com.github.drbookings.SettingsManager;
import com.github.drbookings.UICleaningData;
import com.github.drbookings.ser.CleaningBeanSer;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddCleaningController implements Initializable {

    @SuppressWarnings("unused")
    private final static Logger logger = LoggerFactory.getLogger(AddCleaningController.class);

    @FXML
    Button buttonOK;

    @FXML
    ComboBox<String> comboBoxRoom;

    @FXML
    DatePicker date;

    @FXML
    TextField name;

    @FXML
    CheckBox tax;

    public AddCleaningController() {

    }

    /**
     * Adds a new cleaning entry to the cleaning data.
     *
     * @see UICleaningData
     */
    void addNewEntry() {

	final CleaningBeanSer data = buildCleaningBeanSer();
	// SynchronousEvent e = new AddCleaningEvent(data);
	// Events.getInstance().emit(e);

    }

    public static String removePrefix(final String str) {
	return StringUtils.removeStart(str, SettingsManager.getInstance().getRoomNamePrefix());
    }

    private CleaningBeanSer buildCleaningBeanSer() {
	final CleaningBeanSer cb = new CleaningBeanSer();
	cb.black = !tax.isSelected();
	cb.date = date.getValue();
	cb.name = name.getText();
	cb.room = removePrefix(comboBoxRoom.getSelectionModel().getSelectedItem());
	return cb;

    }

    /**
     * Binds the disable property of {@code buttonOK} to be only enabled if input is
     * valid.
     */
    private void createBindings() {
	buttonOK.disableProperty()
		.bind(Bindings.isNull(date.valueProperty()).or(Bindings.length(name.textProperty()).isEqualTo(0))
			.or(Bindings.isNull(comboBoxRoom.getSelectionModel().selectedItemProperty())));

    }

    /**
     * Fills the combox with room names. Room names are taken from the
     * {@link SettingsManager}.
     */
    private void fillRoomComboBox() {
	final List<String> numbers = new ArrayList<>();
	final String roomPrefix = SettingsManager.getInstance().getRoomNamePrefix();
	for (int i = 1; i <= SettingsManager.getInstance().getNumberOfRooms(); i++)
	    numbers.add(roomPrefix + i);
	comboBoxRoom.getItems().addAll(numbers);

    }

    public DatePicker getDate() {
	return date;
    }

    /**
     * Adds a new entry and closes this stage.
     */
    @FXML
    void handleButtonOK(final ActionEvent event) {

	addNewEntry();
	final Stage stage = (Stage) buttonOK.getScene().getWindow();
	stage.close();
    }

    /**
     * Fills the combobox and creates bindings.
     */
    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	fillRoomComboBox();
	createBindings();
    }

    public void setDate(final DatePicker date) {
	this.date = date;
    }

}

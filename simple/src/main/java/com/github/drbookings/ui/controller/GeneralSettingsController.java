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

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class GeneralSettingsController implements Initializable {

	private final static Logger logger = LoggerFactory.getLogger(GeneralSettingsController.class);

	@FXML
	private TextField cleaningFee;

	@FXML
	private TextField cleaningPlanLookBehind;

	@FXML
	private CheckBox completePayment;

	// @FXML
	// private CheckBox hideCleaningStatistics;

	@FXML
	private CheckBox netEarnings;

	@FXML
	private TextField upcomingLookAhead;

	@FXML
	public void handleActionSaveSettings(final ActionEvent event) {
		saveCleaningPlanLookBehind();
		saveCleaningFee();
		saveUpcomingLookAhead();
		saveCompletePayment();
		saveNetEarnings();
		// saveHideCleaningStatistics();
		SettingsManager.getInstance().saveToFile();
		// final Stage stage = (Stage)
		// cleaningPlanLookBehind.getScene().getWindow();
		// stage.close();
	}

	private void initCleaningFee() {
		cleaningFee.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
		cleaningFee.setPromptText("default " + SettingsManager.DEFAULT_CLEANING_FEES);
		final float value = SettingsManager.getInstance().getCleaningFees();
		if (value != SettingsManager.DEFAULT_CLEANING_FEES) {
			cleaningFee.setText(value + "");
		}
	}

	private void initCleaningPlanLookBehind() {
		cleaningPlanLookBehind.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
		cleaningPlanLookBehind.setPromptText("default " + SettingsManager.DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);
		final int value = SettingsManager.getInstance().getCleaningPlanLookBehind();
		if (value != SettingsManager.DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS) {
			cleaningPlanLookBehind.setText(value + "");
		}
	}

	private void initCompletePayment() {
		completePayment.setSelected(SettingsManager.getInstance().isCompletePayment());
	}

	// private void initHideCleaningStatistics() {
	// hideCleaningStatistics.setSelected(SettingsManager.getInstance().isHideCleaningStatistics());
	// }

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		initCleaningPlanLookBehind();
		initCleaningFee();
		initUpcomingLookAhead();
		initCompletePayment();
		initNetEarinings();
		// initHideCleaningStatistics();
	}

	private void initNetEarinings() {
		netEarnings.setSelected(SettingsManager.getInstance().isShowNetEarnings());

	}

	private void initUpcomingLookAhead() {
		upcomingLookAhead.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
		upcomingLookAhead.setPromptText("default " + SettingsManager.DEFAULT_UPCOMING_LOOK_AHEAD_DAYS);
		final int value = SettingsManager.getInstance().getUpcomingLookAhead();
		if (value != SettingsManager.DEFAULT_UPCOMING_LOOK_AHEAD_DAYS) {
			upcomingLookAhead.setText(value + "");
		}

	}

	private void saveCleaningFee() {
		final String value = cleaningFee.getText();
		if (value != null) {
			try {
				final float value2 = Float.parseFloat(value.trim());
				SettingsManager.getInstance().setCleaningFees(value2);
			} catch (final NumberFormatException e) {
				if (logger.isInfoEnabled()) {
					logger.info("Invalid input " + value);
				}
			}
		}
	}

	private void saveCleaningPlanLookBehind() {
		final String value = cleaningPlanLookBehind.getText();
		if (value != null) {
			try {
				final int value2 = Integer.parseInt(value.trim());
				SettingsManager.getInstance().setCleaningPlanLookBehind(value2);
			} catch (final NumberFormatException e) {
				if (logger.isInfoEnabled()) {
					logger.info("Invalid input " + value);
				}
			}
		}
	}

	private void saveCompletePayment() {
		SettingsManager.getInstance().setCompletePayment(completePayment.isSelected());
	}

	// private void saveHideCleaningStatistics() {
	// SettingsManager.getInstance().setHideCleaningStatistics(hideCleaningStatistics.isSelected());
	// }

	private void saveNetEarnings() {
		SettingsManager.getInstance().setShowNetEarnings(netEarnings.isSelected());
	}

	private void saveUpcomingLookAhead() {
		final String value = upcomingLookAhead.getText();
		if (value != null) {
			try {
				final int value2 = Integer.parseInt(value.trim());
				SettingsManager.getInstance().setUpcomingLookAhead(value2);
			} catch (final NumberFormatException e) {
				if (logger.isInfoEnabled()) {
					logger.info("Invalid input " + value);
				}
			}
		}

	}
}

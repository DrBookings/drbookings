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
    private TextField cleaningPlanLookBehind;

    @FXML
    private TextField upcomingLookAhead;

    @FXML
    private TextField cleaningFee;

    @FXML
    private CheckBox completePayment;

    @FXML
    public void handleActionSaveSettings(final ActionEvent event) {
	saveCleaningPlanLookBehind();
	saveCleaningFee();
	saveUpcomingLookAhead();
	saveCompletePayment();
	// final Stage stage = (Stage)
	// cleaningPlanLookBehind.getScene().getWindow();
	// stage.close();
    }

    private void initCleaningFee() {
	cleaningFee.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
	cleaningFee.setPromptText("default " + SettingsManager.DEFAULT_CLEANING_FEE);
	final float value = SettingsManager.getInstance().getCleaningFees();
	if (value != SettingsManager.DEFAULT_CLEANING_FEE) {
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

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	initCleaningPlanLookBehind();
	initCleaningFee();
	initUpcomingLookAhead();
	initCompletePayment();
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
		final int value2 = Integer.parseInt(value.trim());
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

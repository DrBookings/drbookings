package com.github.drbookings.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class GeneralSettingsController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(GeneralSettingsController.class);

    @FXML
    private TextField cleaningPlanLookBehind;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	cleaningPlanLookBehind.setStyle("-fx-prompt-text-fill: derive(-fx-control-inner-background, -30%);");
	cleaningPlanLookBehind.setPromptText("[days], default " + SettingsManager.DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS);

	final int value = SettingsManager.getInstance().getCleaningPlanLookBehind();
	if (value != SettingsManager.DEFAULT_CLEANINGPLAN_LOOKBEHIND_DAYS) {
	    cleaningPlanLookBehind.setText(value + "");
	}
	cleaningPlanLookBehind.textProperty().addListener((ChangeListener<String>) (observable, oldValue, newValue) -> {
	    try {
		final int value2 = Integer.parseInt(newValue);
		SettingsManager.getInstance().setCleaningPlanLookBehind(value2);
	    } catch (final NumberFormatException e) {

	    }
	});
    }

}

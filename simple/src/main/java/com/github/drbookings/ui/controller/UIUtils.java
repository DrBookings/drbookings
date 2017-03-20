package com.github.drbookings.ui.controller;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;

public class UIUtils {

    private static void doShowError(final String shortMsg, final String msg) {
	final Alert alert = new Alert(AlertType.ERROR);
	alert.setTitle("Error");
	alert.setHeaderText(shortMsg);
	final Label label = new Label(msg);
	label.setWrapText(true);
	alert.getDialogPane().setContent(label);
	alert.showAndWait();
    }

    public static void showError(final String shortMsg, final String msg) {
	Platform.runLater(() -> doShowError(shortMsg, msg));

    }

}

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

package com.github.drbookings.ui;

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

    public static void showError(final Throwable t) {
	Platform.runLater(() -> doShowError(t.getLocalizedMessage(), t.toString()));

    }

}

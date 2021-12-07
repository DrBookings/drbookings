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

import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class FXUIUtils {

    private FXUIUtils() {

    }

    public static void centerStageOnScreen(Stage stage, Node node) {
	final Stage windowStage = (Stage) node.getScene().getWindow();
	stage.setX(windowStage.getX() + windowStage.getWidth() / 2 - stage.getWidth() / 2);
	stage.setY((windowStage.getY() + windowStage.getHeight()) / 2 - stage.getHeight() / 2);
    }

    public static Pair<Stage, FXMLLoader> buildStageFromFxml2(URL fxmlResource, String title, double width,
	    double height)
	    throws IOException {
	final FXMLLoader loader = new FXMLLoader(fxmlResource);
	final Parent root = loader.load();
	final Scene scene = new Scene(root);
	final Stage stage = new Stage();
	stage.setWidth(width);
	stage.setHeight(height);
	stage.setTitle(title);
	stage.setScene(scene);
	return new ImmutablePair<Stage, FXMLLoader>(stage, loader);
    }

    public static Stage buildStageFromFxml(URL fxmlResource, String title, double width, double height)
	    throws IOException {

	return buildStageFromFxml2(fxmlResource, title, width, height).getLeft();
    }

    static void doShowError(final String shortMsg, final String msg) {
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

package com.github.drbookings.ui;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class DrBookingsApplication extends Application {

    private final static Logger logger = LoggerFactory.getLogger(DrBookingsApplication.class);

    public static void main(final String[] args) {
	launch(args);
    }

    private MainController mainController;

    @Override
    public void start(final Stage stage) throws Exception {
	final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
	final Parent root = loader.load();
	final Scene scene = new Scene(root, 900, 800);
	stage.setTitle("Dr.Bookings");
	stage.setScene(scene);
	stage.setOnCloseRequest(event -> {
	    final Alert alert = new Alert(AlertType.CONFIRMATION);
	    final ButtonType buttonTypeOne = new ButtonType("Yes");
	    final ButtonType buttonTypeTwo = new ButtonType("No");
	    final ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
	    alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeTwo, buttonTypeOne);
	    alert.setTitle("Save changes?");
	    alert.setHeaderText("Save changes?");

	    final Optional<ButtonType> result = alert.showAndWait();

	    if (result.get() == buttonTypeOne) {
		new Thread(() -> mainController.saveState()).start();
		// go ahead..
	    } else if (result.get() == buttonTypeTwo) {
		// go ahead..
	    } else {
		// cancel shutdown
		event.consume();
	    }
	});
	mainController = loader.getController();
	stage.show();

    }

    @Override
    public void stop() throws Exception {
	mainController.shutDown();
	super.stop();
    }
}

package com.github.drbookings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
	final Scene scene = new Scene(root, 800, 800);
	stage.setTitle("Dr.Bookings");
	stage.setScene(scene);
	mainController = loader.getController();
	stage.show();

    }

    @Override
    public void stop() throws Exception {
	super.stop();
	mainController.shutDown();
    }

}

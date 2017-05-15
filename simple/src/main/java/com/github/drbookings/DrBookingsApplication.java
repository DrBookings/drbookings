package com.github.drbookings;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.controller.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DrBookingsApplication extends Application {

    private final static Logger logger = LoggerFactory.getLogger(DrBookingsApplication.class);

    public static void main(final String[] args) {
	launch(args);
    }

    private MainController mainController;

    @Override
    public void start(final Stage stage) throws Exception {
	if (logger.isInfoEnabled()) {
	    logger.info("Application version " + getClass().getPackage().getImplementationVersion());
	}
	final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
	final Parent root = loader.load();
	final Scene scene = new Scene(root, 700, 800);
	String s = getClass().getPackage().getImplementationVersion();
	if (s == null) {
	    s = "dev version";
	}
	stage.setTitle("Dr.Bookings " + s);
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
		try {
		    final FileChooser fileChooser = new FileChooser();
		    final File file = SettingsManager.getInstance().getDataFile();
		    fileChooser.setInitialDirectory(file.getParentFile());
		    fileChooser.getExtensionFilters().addAll(
			    new FileChooser.ExtensionFilter("Dr.Booking Booking Data", Arrays.asList("*.xml")),
			    new FileChooser.ExtensionFilter("All Files", "*"));
		    fileChooser.setTitle("Select File");
		    fileChooser.setInitialFileName(file.getName());
		    final File file2 = fileChooser.showSaveDialog(scene.getWindow());
		    if (file2 != null) {
			SettingsManager.getInstance().setDataFile(file2);
			new XMLStorage().save(mainController.getManager(), file2);
		    }
		} catch (final Exception e) {
		    logger.error(e.getLocalizedMessage(), e);
		}
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

package com.github.drbookings.ui.dialogs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.controller.CleaningPlanController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class CleaningPlanDialogFactory implements ViewFactory {

    private static final Logger logger = LoggerFactory.getLogger(CleaningPlanDialogFactory.class);

    private final MainManager manager;

    public CleaningPlanDialogFactory(final MainManager manager) {
	super();
	this.manager = manager;
    }

    @Override
    public void showDialog() {
	try {
	    final FXMLLoader loader = new FXMLLoader(
		    CleaningPlanDialogFactory.class.getResource("/fxml/CleaningPlanView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root);
	    stage.setTitle("Cleaning Plan");
	    stage.setScene(scene);
	    stage.setWidth(400);
	    stage.setHeight(600);
	    final CleaningPlanController c = loader.getController();
	    c.setManager(manager);
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }
}

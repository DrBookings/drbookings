package com.github.drbookings.ui.dialogs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AbstractDialogFactory implements DialogFactory {

    private static final Logger logger = LoggerFactory.getLogger(AbstractDialogFactory.class);

    private String fxml;

    private String title;

    private int width = 200;

    private int height = 200;

    protected Stage stage;

    protected Scene scene;

    public String getFxml() {
	return fxml;
    }

    public int getHeight() {
	return height;
    }

    public String getTitle() {
	return title;
    }

    public int getWidth() {
	return width;
    }

    public AbstractDialogFactory setFxml(final String fxml) {
	this.fxml = fxml;
	return this;
    }

    public AbstractDialogFactory setHeight(final int height) {
	this.height = height;
	return this;
    }

    public AbstractDialogFactory setTitle(final String title) {
	this.title = title;
	return this;
    }

    public AbstractDialogFactory setWidth(final int width) {
	this.width = width;
	return this;
    }

    @Override
    public void showDialog() {
	try {
	    final FXMLLoader loader = new FXMLLoader(AbstractDialogFactory.class.getResource(fxml));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root);
	    stage.setTitle(title);
	    stage.setScene(scene);
	    stage.setWidth(400);
	    stage.setHeight(600);
	    visitController(loader.getController());
	    stage.show();
	    this.stage = stage;
	    this.scene = scene;
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    protected void visitController(final Object controller) {

    }
}

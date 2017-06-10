package com.github.drbookings.ui.dialogs;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AbstractViewFactory implements ViewFactory {

	private static final Logger logger = LoggerFactory.getLogger(AbstractViewFactory.class);

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

	public AbstractViewFactory setFxml(final String fxml) {
		this.fxml = fxml;
		return this;
	}

	public AbstractViewFactory setHeight(final int height) {
		this.height = height;
		return this;
	}

	public AbstractViewFactory setTitle(final String title) {
		this.title = title;
		return this;
	}

	public AbstractViewFactory setWidth(final int width) {
		this.width = width;
		return this;
	}

	@Override
	public void showDialog() {
		if (this.stage != null) {
			if (logger.isDebugEnabled()) {
				logger.debug(this.stage + " already created");
			}
			this.stage.show();
			this.stage.requestFocus();
		} else {
			try {
				final FXMLLoader loader = new FXMLLoader(AbstractViewFactory.class.getResource(fxml));
				final Parent root = loader.load();
				final Stage stage = new Stage();
				final Scene scene = new Scene(root);
				stage.setTitle(title);
				stage.setScene(scene);
				stage.setWidth(getWidth());
				stage.setHeight(getHeight());
				stage.setMinHeight(100);
				stage.setMinWidth(100);
				visitController(loader.getController());
				this.stage = stage;
				this.scene = scene;
				visitStage(this.stage);
				stage.show();
			} catch (final IOException e) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
	}

	protected void visitStage(final Stage stage) {
		// TODO Auto-generated method stub

	}

	protected void visitController(final Object controller) {

	}
}

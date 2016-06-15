/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package com.github.drbookings.parts;

import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.di.Persist;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.FXMLController;
import com.github.drbookings.core.api.BookingManager;

import javafx.application.Platform;
import javafx.embed.swt.FXCanvas;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class SamplePart {

	private final static Logger logger = LoggerFactory.getLogger(SamplePart.class);
	@Inject
	private MDirtyable dirty;

	private FXCanvas fxCanvas;

	private FXMLController fxmlController;

	@Inject
	private BookingManager manager;

	@PostConstruct
	public void createComposite(final Composite parent) {
		// parent.setLayout(new GridLayout(1, false));
		init(parent);

	}

	protected void createScene(final Composite parent) {

		if (logger.isInfoEnabled()) {
			logger.info("Initializing FX");
		}
		try {
			final URL location = getClass().getResource("/fxml/FXML.fxml");
			final FXMLLoader fXMLLoader = new FXMLLoader();
			fXMLLoader.setLocation(location);
			fXMLLoader.setBuilderFactory(new JavaFXBuilderFactory());
			final Parent root = fXMLLoader.load(location.openStream());
			fxmlController = fXMLLoader.getController();

			// fxmlController.getZoomManager().start();
			final Scene scene = new Scene(root);
			fxCanvas.setScene(scene);

			if (logger.isInfoEnabled()) {
				logger.info("Initializing FX successful");
			}
		} catch (final Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error(e.getLocalizedMessage(), e);
			}
		}
		fxmlController.getTable().setItems(manager.getBookings());
	}

	protected void init(final Composite parent) {

		// this will initialize the FX Toolkit
		fxCanvas = new FXCanvas(parent, SWT.NONE);
		Platform.setImplicitExit(false);
		Platform.runLater(() -> createScene(parent));
	}

	@Persist
	public void save() {

	}

	@Focus
	public void setFocus() {
		if (fxCanvas != null) {
			fxCanvas.setFocus();
		}
	}
}
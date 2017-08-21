package com.github.drbookings.ui.dialogs;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

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

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

package com.github.drbookings.ui.dialogs;

import com.github.drbookings.ui.controller.MainController;
import com.github.drbookings.ui.controller.RoomDetailsController;

import javafx.stage.Stage;

public class RoomDetailsDialogFactory extends AbstractViewFactory {

    private final MainController manager;

    private RoomDetailsController c;

    public RoomDetailsDialogFactory(final MainController manager) {
	this.manager = manager;
	setFxml("/fxml/RoomDetailsView.fxml");
	setTitle("Room Details");
	setHeight(200);
	setWidth(300);

    }

    @Override
    public void showDialog() {
	super.showDialog();
    }

    @Override
    protected void visitController(final Object controller) {
	super.visitController(controller);
	this.c = (RoomDetailsController) controller;
	c.setManager(manager);
    }

    @Override
    protected void visitStage(final Stage stage) {
	stage.setOnCloseRequest(event -> c.shutDown());
    }
}

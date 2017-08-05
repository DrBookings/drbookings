package com.github.drbookings.ui.dialogs;

import com.github.drbookings.ui.controller.MainController;
import com.github.drbookings.ui.controller.RoomDetailsController;

import javafx.stage.Stage;

public class RoomDetailsDialogFactory extends AbstractViewFactory {

	private final MainController manager;

	public RoomDetailsDialogFactory(final MainController manager) {
		this.manager = manager;
		setFxml("/fxml/RoomDetailsView.fxml");
		setTitle("Room Details");
		setHeight(200);
		setWidth(300);

	}

	private RoomDetailsController c;

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

	@Override
	public void showDialog() {
		super.showDialog();
	}
}

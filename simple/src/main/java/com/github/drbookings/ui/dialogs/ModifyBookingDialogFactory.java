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

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.controller.ModifyBookingController;

public class ModifyBookingDialogFactory extends AbstractViewFactory {

	private final MainManager manager;

	public ModifyBookingDialogFactory(final MainManager manager) {
		this.manager = manager;
		setTitle("Modify Booking");
		setFxml("/fxml/ModifyBookingView.fxml");
		setWidth(250);
		setHeight(350);
	}

	@Override
	protected void visitController(final Object controller) {
		final ModifyBookingController c = (ModifyBookingController) controller;
		c.setManager(manager);

	}

}

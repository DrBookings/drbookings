/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.controller.BookingDetailsController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookingDetailsDialogFactory extends AbstractViewFactory implements ViewFactory {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(BookingDetailsDialogFactory.class);
	private final MainManager manager;

	public BookingDetailsDialogFactory(final MainManager manager) {
		this.manager = manager;
		setFxml("/fxml/BookingDetailsView.fxml");
        setTitle("BookingBean Details");
		setHeight(600);
		setWidth(520);
	}

	@Override
	protected void visitController(final Object controller) {
		final BookingDetailsController c = (BookingDetailsController) controller;
		c.setManager(manager);
	}
}

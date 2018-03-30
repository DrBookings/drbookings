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

package com.github.drbookings.ui;

import java.text.NumberFormat;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FormattedNumberCellValueFactory<T> implements Callback<TableColumn<T, Number>, TableCell<T, Number>> {

	private final NumberFormat nf;

	public FormattedNumberCellValueFactory(final NumberFormat nf) {
		super();
		this.nf = nf;
	}

	@Override
	public TableCell<T, Number> call(final TableColumn<T, Number> param) {
		return new TableCell<T, Number>() {

			@Override
			protected void updateItem(final Number item, final boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(nf.format(item));
				}
			}
		};
	}
}

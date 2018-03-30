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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.controller.CellContentController;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class StudioCellFactory implements Callback<TableColumn<DateBean, DateBean>, TableCell<DateBean, DateBean>> {

	private static final Logger logger = LoggerFactory.getLogger(StudioCellFactory.class);

	private final String id;

	public StudioCellFactory(final String id) {
		this.id = id;
	}

	@SuppressWarnings("unused")
	private Node buildCellContent(final RoomBean roomBean) {
		try {
			final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CellContentView.fxml"));
			final VBox cellContent = loader.load();
			final CellContentController c = loader.getController();
			c.setData(roomBean);
			return cellContent;
		} catch (final IOException e) {
			logger.error(e.getLocalizedMessage(), e);

		}
		return null;
	}

	private Node buildCellContentFast(final RoomBean roomBean) {
		final CellContentController c = new CellContentController();
		final VBox parent = new VBox(0);
		// parent.setMaxHeight(Double.POSITIVE_INFINITY);
		// parent.setAlignment(Pos.CENTER);
		c.setCellContainer(parent);
		c.setData(roomBean);

		return parent;
	}

	@Override
	public TableCell<DateBean, DateBean> call(final TableColumn<DateBean, DateBean> param) {
		return new TableCell<DateBean, DateBean>() {

			@Override
			protected void updateItem(final DateBean item, final boolean empty) {

				super.updateItem(item, empty);
				if (empty || item == null) {
					setText("");
					setStyle("");
					setGraphic(null);
				} else {
					setGraphic(buildCellContentFast(item.getRoom(id)));
					setStyle("-fx-padding: 0 0 0 0;");
				}
			}
		};
	}

}

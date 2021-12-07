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

import com.github.drbookings.DateBean;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class OccupancyCellFactory implements Callback<TableColumn<DateBean, Number>, TableCell<DateBean, Number>> {

    @Override
    public TableCell<DateBean, Number> call(final TableColumn<DateBean, Number> param) {
	return new TableCell<DateBean, Number>() {

	    @Override
	    protected void updateItem(final Number item, final boolean empty) {

		super.updateItem(item, empty);
		getStyleClass().removeAll("occupancy-low", "occupancy-medium", "occupancy-high", "occupancy-default");
		if (empty || (item == null)) {
		    setText(null);
		    setStyle("");
		    setGraphic(null);
		} else {
		    setText(String.format("%4.0f%%", item.floatValue() * 100));
		    if (item.floatValue() < 0.40) {
			getStyleClass().add("occupancy-low");
		    } else if (item.floatValue() > 0.60) {
			getStyleClass().add("occupancy-high");
		    } else {
			getStyleClass().add("occupancy-medium");
		    }
		    // System.err.println(getStyleClass());
		}
	    }
	};
    }

}

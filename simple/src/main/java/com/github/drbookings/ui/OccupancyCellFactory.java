package com.github.drbookings.ui;

import com.github.drbookings.model.bean.DateBean;

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
		if (empty || item == null) {
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

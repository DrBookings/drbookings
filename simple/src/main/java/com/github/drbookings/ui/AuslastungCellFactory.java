package com.github.drbookings.ui;

import com.github.drbookings.model.bean.DateBean;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class AuslastungCellFactory implements Callback<TableColumn<DateBean, Number>, TableCell<DateBean, Number>> {

    @Override
    public TableCell<DateBean, Number> call(final TableColumn<DateBean, Number> param) {
	return new TableCell<DateBean, Number>() {

	    @Override
	    protected void updateItem(final Number item, final boolean empty) {

		super.updateItem(item, empty);
		if (empty || item == null) {
		    setText(null);
		    setStyle("");
		    setGraphic(null);
		} else {
		    setText(String.format("%4.0f%%", item.floatValue() * 100));
		    if (item.floatValue() < 0.33) {
			setStyle("-fx-background-color:coral;-fx-alignment: CENTER;-fx-opacity:0.6;");
		    } else if (item.floatValue() < 0.50) {
			setStyle("-fx-background-color:orange;-fx-alignment: CENTER;-fx-opacity:0.5;");
		    } else if (item.floatValue() > 0.90) {
			setStyle("-fx-background-color:limegreen;-fx-alignment: CENTER;-fx-opacity:0.6;");
		    } else if (item.floatValue() > 0.66) {
			setStyle("-fx-background-color:yellowgreen;-fx-alignment: CENTER;-fx-opacity:0.6;");
		    } else {
			setStyle("-fx-alignment: CENTER;-fx-opacity:0.6;");
		    }
		}
	    }
	};
    }

}

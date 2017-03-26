package com.github.drbookings.ui;

import com.github.drbookings.model.bean.DateBean;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class OccupancyCellValueFactory
	implements Callback<CellDataFeatures<DateBean, Number>, ObservableValue<Number>> {

    @Override
    public ObservableValue<Number> call(final CellDataFeatures<DateBean, Number> param) {
	return param.getValue().auslastungProperty();
    }

}

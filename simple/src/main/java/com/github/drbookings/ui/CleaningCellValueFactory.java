package com.github.drbookings.ui;

import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;

public class CleaningCellValueFactory implements Callback<CellDataFeatures<DateBean, String>, ObservableValue<String>> {

    private final String id;

    public CleaningCellValueFactory(final String id) {
	this.id = id;
    }

    @Override
    public ObservableValue<String> call(final CellDataFeatures<DateBean, String> param) {
	final RoomBean room = param.getValue().getRoom(id);
	return room.cleaningProperty();
    }

}

package com.github.drbookings;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.RoomBean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class RoomSelectionManager {

    private static class InstanceHolder {
	private static final RoomSelectionManager instance = new RoomSelectionManager();
    }

    private static final Logger logger = LoggerFactory.getLogger(RoomSelectionManager.class);

    public static RoomSelectionManager getInstance() {
	return InstanceHolder.instance;
    }

    private final ObservableList<RoomBean> data = FXCollections.observableArrayList(RoomBean.extractor());

    private RoomSelectionManager() {
    }

    public ObservableList<RoomBean> getSelection() {
	return data;
    }

    public void setSelection(final Collection<? extends RoomBean> data) {
	this.data.setAll(data);
    }

}

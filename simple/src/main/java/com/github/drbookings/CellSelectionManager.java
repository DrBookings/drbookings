package com.github.drbookings;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.RoomBean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class CellSelectionManager {

    private static class InstanceHolder {
	private static final CellSelectionManager instance = new CellSelectionManager();
    }

    private static final Logger logger = LoggerFactory.getLogger(CellSelectionManager.class);

    public static CellSelectionManager getInstance() {
	return InstanceHolder.instance;
    }

    private final ObservableList<RoomBean> data = FXCollections.observableArrayList(RoomBean.extractor());

    private CellSelectionManager() {
    }

    public ObservableList<RoomBean> getSelection() {
	return data;
    }

    public void setSelection(final Collection<? extends RoomBean> data) {
	this.data.setAll(data);
	if (logger.isDebugEnabled()) {
	    logger.debug("Selection now " + data);
	}
    }

}

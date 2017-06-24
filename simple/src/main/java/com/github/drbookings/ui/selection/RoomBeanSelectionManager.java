package com.github.drbookings.ui.selection;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.beans.RoomBean;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

public class RoomBeanSelectionManager {

	private static class InstanceHolder {
		private static final RoomBeanSelectionManager instance = new RoomBeanSelectionManager();
	}

	private static final Logger logger = LoggerFactory.getLogger(RoomBeanSelectionManager.class);

	public static RoomBeanSelectionManager getInstance() {
		return InstanceHolder.instance;
	}

	private final ListProperty<RoomBean> selection = new SimpleListProperty<>(
			FXCollections.observableArrayList(RoomBean.extractor()));

	private RoomBeanSelectionManager() {

	}

	public final ListProperty<RoomBean> selectionProperty() {
		return this.selection;
	}

	public final List<RoomBean> getSelection() {
		return this.selectionProperty().get();
	}

	public void setSelection(final Collection<? extends RoomBean> data) {
		this.selection.setAll(data);
		if (logger.isDebugEnabled()) {
			logger.debug("Selection updated: " + data.size());
		}
	}

}

package com.github.drbookings.ui.selection;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

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

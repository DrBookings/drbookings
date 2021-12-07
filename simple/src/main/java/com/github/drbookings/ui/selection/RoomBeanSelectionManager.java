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

package com.github.drbookings.ui.selection;

import com.github.drbookings.RoomBean;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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

    public final List<RoomBean> getSelection() {
	return selectionProperty().get();
    }

    public final ListProperty<RoomBean> selectionProperty() {
	return selection;
    }

    public void setSelection(final Collection<? extends RoomBean> data) {
	selection.setAll(data);
	// if (logger.isDebugEnabled()) {
	// logger.debug("Selection updated: " + data.size());
	// }
    }

    public Optional<RoomBean> getFirstSelected() {
	final List<RoomBean> rooms = selectionProperty();
	if (rooms.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(rooms.get(0));
    }

}

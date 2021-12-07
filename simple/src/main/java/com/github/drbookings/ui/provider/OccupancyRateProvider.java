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

package com.github.drbookings.ui.provider;

import com.github.drbookings.OccupancyRateCalculator;
import com.github.drbookings.RoomBean;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.ObservableList;

import java.util.concurrent.Callable;

public class OccupancyRateProvider {

    private static final OccupancyRateCalculator c = new OccupancyRateCalculator();

    private final FloatProperty occupancyRate = new SimpleFloatProperty();

    public OccupancyRateProvider() {
	bindProperties();
    }

    private void bindProperties() {
	occupancyRate.bind(Bindings.createObjectBinding(calculateOccupancyRate(),
		RoomBeanSelectionManager.getInstance().selectionProperty()));
    }

    private Callable<Number> calculateOccupancyRate() {
	return () -> {
	    final ObservableList<RoomBean> rooms = RoomBeanSelectionManager.getInstance().selectionProperty();
	    return c.apply(rooms);
	};
    }

    public final float getOccupancyRate() {
	return this.occupancyRateProperty().get();
    }

    public final FloatProperty occupancyRateProperty() {
	return this.occupancyRate;
    }

    public final void setOccupancyRate(final float occupancyRate) {
	this.occupancyRateProperty().set(occupancyRate);
    }
}

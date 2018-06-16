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

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.MinimumPriceCalulcator;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class MinimumPriceProvider {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(MinimumPriceProvider.class);

    private final FloatProperty minimumPrice = new SimpleFloatProperty();

    private final OccupancyRateProvider occupancyRateProvider = new OccupancyRateProvider();

    private final ReferenceIncomeProvider referenceIncomeProvider = new ReferenceIncomeProvider();

    public MinimumPriceProvider() {
	bindProperties();
    }

    private void bindProperties() {
	minimumPrice.bind(Bindings.createObjectBinding(calculateMinimumPrice(),
		referenceIncomeProvider.referenceIncomeProperty(), occupancyRateProvider.occupancyRateProperty(),
		RoomBeanSelectionManager.getInstance().selectionProperty()));
    }

    private Callable<Number> calculateMinimumPrice() {
	return () -> {
	    return new MinimumPriceCalulcator(referenceIncomeProvider.get())
		    .apply(RoomBeanSelectionManager.getInstance().getSelection());

	};
    }

    public final float getMinimumPrice() {
	return this.minimumPriceProperty().get();
    }

    public OccupancyRateProvider getOccupancyRateProvider() {
	return occupancyRateProvider;
    }

    public final FloatProperty minimumPriceProperty() {
	return this.minimumPrice;
    }

}

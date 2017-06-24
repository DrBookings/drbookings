package com.github.drbookings.ui.provider;

import java.util.concurrent.Callable;

import com.github.drbookings.model.OccupancyRateCalculator;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.collections.ObservableList;

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

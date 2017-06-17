package com.github.drbookings.ui.provider;

import java.util.concurrent.Callable;

import com.github.drbookings.model.OccupancyRateCalculator;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.beans.RoomBean;

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
				CellSelectionManager.getInstance().getSelection()));
	}

	private Callable<Number> calculateOccupancyRate() {
		return () -> {
			final ObservableList<RoomBean> rooms = CellSelectionManager.getInstance().getSelection();
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

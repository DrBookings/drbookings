package com.github.drbookings.model;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.CellSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class MinimumPriceProvider {

	private static final Logger logger = LoggerFactory.getLogger(MinimumPriceProvider.class);

	private final FloatProperty minimumPrice = new SimpleFloatProperty();

	private final ReferenceIncomeProvider referenceIncomeProvider = new ReferenceIncomeProvider();

	private final OccupancyRateProvider occupancyRateProvider = new OccupancyRateProvider();

	public OccupancyRateProvider getOccupancyRateProvider() {
		return occupancyRateProvider;
	}

	public MinimumPriceProvider() {
		bindProperties();
	}

	private void bindProperties() {
		minimumPrice.bind(Bindings.createObjectBinding(calculateMinimumPrice(),
				referenceIncomeProvider.referenceIncomeProperty(), occupancyRateProvider.occupancyRateProperty(),
				CellSelectionManager.getInstance().getSelection()));
	}

	private Callable<Number> calculateMinimumPrice() {
		return () -> {
			return new MinimumPriceCalulcator(referenceIncomeProvider.get())
					.apply(CellSelectionManager.getInstance().getSelection());

		};
	}

	public final float getMinimumPrice() {
		return this.minimumPriceProperty().get();
	}

	public final FloatProperty minimumPriceProperty() {
		return this.minimumPrice;
	}

}

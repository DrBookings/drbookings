package com.github.drbookings.ui.provider;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.MinimumPriceCalulcator;
import com.github.drbookings.ui.CellSelectionManager;

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

	public OccupancyRateProvider getOccupancyRateProvider() {
		return occupancyRateProvider;
	}

	public final FloatProperty minimumPriceProperty() {
		return this.minimumPrice;
	}

}

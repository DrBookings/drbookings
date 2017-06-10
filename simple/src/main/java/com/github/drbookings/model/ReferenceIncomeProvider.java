package com.github.drbookings.model;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.github.drbookings.model.settings.SettingsManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;

public class ReferenceIncomeProvider implements Supplier<Number> {

	private final FloatProperty referenceIncome = new SimpleFloatProperty();

	public ReferenceIncomeProvider() {
		referenceIncomeProperty().bind(Bindings.createObjectBinding(calcRefIncome(),
				SettingsManager.getInstance().referenceColdRentLongTermProperty(),
				SettingsManager.getInstance().additionalCostsProperty(),
				SettingsManager.getInstance().numberOfRoomsProperty()));
	}

	private Callable<Number> calcRefIncome() {
		return () -> {
			final double refColdRent = SettingsManager.getInstance().getReferenceColdRentLongTerm();
			final double addCosts = SettingsManager.getInstance().getAdditionalCosts();
			final double roomCnt = SettingsManager.getInstance().getNumberOfRooms();
			double result = 0;
			result += refColdRent;
			result += addCosts;
			result *= roomCnt;
			return result;
		};
	}

	@Override
	public Number get() {
		return referenceIncomeProperty().getValue();
	}

	public final FloatProperty referenceIncomeProperty() {
		return this.referenceIncome;
	}

	public final float getReferenceIncome() {
		return this.referenceIncomeProperty().get();
	}

	public final void setReferenceIncome(final float referenceIncome) {
		this.referenceIncomeProperty().set(referenceIncome);
	}

}

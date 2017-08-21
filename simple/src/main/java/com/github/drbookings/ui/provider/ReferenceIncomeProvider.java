package com.github.drbookings.ui.provider;

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

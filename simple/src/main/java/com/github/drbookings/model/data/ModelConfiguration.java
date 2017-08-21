package com.github.drbookings.model.data;

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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ModelConfiguration {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (isNettoIncludesCleaning() ? 1231 : 1237);
	result = prime * result + getNightCounting().hashCode();
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (!(obj instanceof ModelConfiguration)) {
	    return false;
	}
	final ModelConfiguration other = (ModelConfiguration) obj;
	if (isNettoIncludesCleaning() != other.isNettoIncludesCleaning()) {
	    return false;
	}
	if (getNightCounting() != other.getNightCounting()) {
	    return false;
	}
	return true;
    }

    public static enum NightCounting {
	DAY_BEFORE, DAY_AFTER
    }

    public static final NightCounting DEFAULT_NIGHT_COUNTING = NightCounting.DAY_BEFORE;

    public static final boolean DEFAULT_NETTO_INCLUDES_CLEANING = true;

    private final BooleanProperty nettoIncludesCleaning = new SimpleBooleanProperty(DEFAULT_NETTO_INCLUDES_CLEANING);

    private final ObjectProperty<NightCounting> nightCounting = new SimpleObjectProperty<>(DEFAULT_NIGHT_COUNTING);

    public NightCounting getNightCounting() {
	return this.nightCountingProperty().get();
    }

    public boolean isNettoIncludesCleaning() {
	return this.nettoIncludesCleaningProperty().get();
    }

    public BooleanProperty nettoIncludesCleaningProperty() {
	return this.nettoIncludesCleaning;
    }

    public ObjectProperty<NightCounting> nightCountingProperty() {
	return this.nightCounting;
    }

    public void setNettoIncludesCleaning(final boolean nettoIncludesCleaning) {
	this.nettoIncludesCleaningProperty().set(nettoIncludesCleaning);
    }

    public void setNightCounting(final NightCounting nightCounting) {
	this.nightCountingProperty().set(nightCounting);
    }

}

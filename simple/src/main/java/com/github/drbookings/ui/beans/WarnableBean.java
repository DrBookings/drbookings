package com.github.drbookings.ui.beans;

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

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public abstract class WarnableBean {

    protected final BooleanProperty warning = new SimpleBooleanProperty();

    public WarnableBean() {

    }

    protected void bindWarningProperty() {
	warning.bind(Bindings.createBooleanBinding(calculateWarningProperty(), getWarnableObservables()));

    }

    protected abstract Callable<Boolean> calculateWarningProperty();

    protected abstract Observable[] getWarnableObservables();

    protected void bindProperties() {
	bindWarningProperty();
    }

    public boolean isWarning() {
	return this.warningProperty().get();
    }

    /**
     * Property is bound.
     */
    protected void setWarning(final boolean warning) {
	this.warningProperty().set(warning);
    }

    public BooleanProperty warningProperty() {
	return this.warning;
    }
}

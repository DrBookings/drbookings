package com.github.drbookings.model.bean;

import java.util.concurrent.Callable;

import javax.xml.bind.annotation.XmlTransient;

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

    protected void init() {
	bindWarningProperty();
    }

    @XmlTransient
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

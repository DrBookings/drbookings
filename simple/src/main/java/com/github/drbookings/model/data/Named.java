package com.github.drbookings.model.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Named extends IDed {

    private final StringProperty name = new SimpleStringProperty();

    @Override
    public String toString() {
	return getName();
    }

    public Named(final String name) {
	super();
	setName(name);
    }

    public StringProperty nameProperty() {
	return this.name;
    }

    public String getName() {
	return this.nameProperty().get();
    }

    public void setName(final String name) {
	this.nameProperty().set(name);
    }

}

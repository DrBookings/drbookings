package com.github.drbookings.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ModelConfiguration {

    public static enum NightCounting {
	DAY_BEFORE, DAY_AFTER, DAY_BEFORE_AND_AFTER
    }

    public static final NightCounting DEFAULT_NIGHT_COUNTING = NightCounting.DAY_BEFORE;

    private final ObjectProperty<NightCounting> nightCounting = new SimpleObjectProperty<>(DEFAULT_NIGHT_COUNTING);

    public NightCounting getNightCounting() {
	return this.nightCountingProperty().get();
    }

    public ObjectProperty<NightCounting> nightCountingProperty() {
	return this.nightCounting;
    }

    public void setNightCounting(final NightCounting nightCounting) {
	this.nightCountingProperty().set(nightCounting);
    }

}

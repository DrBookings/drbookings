package com.github.drbookings.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ModelConfiguration {

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

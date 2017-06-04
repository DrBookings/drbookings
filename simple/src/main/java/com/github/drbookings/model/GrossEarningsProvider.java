package com.github.drbookings.model;

import javafx.beans.property.ReadOnlyProperty;

public interface GrossEarningsProvider {

    float getGrossEarnings();

    ReadOnlyProperty<Number> grossEarningsProperty();

}

package com.github.drbookings.model;

import javafx.beans.property.ReadOnlyProperty;

public interface NetEarningsProvider {

    float getNetEarnings();

    ReadOnlyProperty<Number> netEarningsProperty();

}

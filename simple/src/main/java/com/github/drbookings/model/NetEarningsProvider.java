package com.github.drbookings.model;

import javafx.beans.property.ReadOnlyProperty;

/**
 * A general provider for net earnings.
 *
 * @author alex
 *
 */
public interface NetEarningsProvider {

	float getNetEarnings();

	ReadOnlyProperty<Number> netEarningsProperty();

}

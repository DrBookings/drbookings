package com.github.drbookings.model;

import javafx.beans.property.ReadOnlyProperty;

/**
 * A general provider for gross earnings.
 * 
 * @author alex
 *
 */
public interface GrossEarningsProvider {

	float getGrossEarnings();

	ReadOnlyProperty<Number> grossEarningsProperty();

}

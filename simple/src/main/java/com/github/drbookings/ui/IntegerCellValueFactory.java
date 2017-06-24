package com.github.drbookings.ui;

import java.text.NumberFormat;

public class IntegerCellValueFactory<T> extends FormattedNumberCellValueFactory<T> {
	public IntegerCellValueFactory() {
		super(NumberFormat.getIntegerInstance());
	}
}

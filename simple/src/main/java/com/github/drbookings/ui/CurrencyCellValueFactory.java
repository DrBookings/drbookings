package com.github.drbookings.ui;

import java.text.DecimalFormat;

public class CurrencyCellValueFactory<T> extends FormattedNumberCellValueFactory<T> {
	public CurrencyCellValueFactory() {
		super(new DecimalFormat("#,###,###,##0.00"));
	}
}

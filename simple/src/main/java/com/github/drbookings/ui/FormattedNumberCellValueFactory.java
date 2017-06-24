package com.github.drbookings.ui;

import java.text.NumberFormat;

import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class FormattedNumberCellValueFactory<T> implements Callback<TableColumn<T, Number>, TableCell<T, Number>> {

	private final NumberFormat nf;

	public FormattedNumberCellValueFactory(final NumberFormat nf) {
		super();
		this.nf = nf;
	}

	@Override
	public TableCell<T, Number> call(final TableColumn<T, Number> param) {
		return new TableCell<T, Number>() {

			@Override
			protected void updateItem(final Number item, final boolean empty) {
				super.updateItem(item, empty);
				if (item == null || empty) {
					setText(null);
				} else {
					setText(nf.format(item));
				}
			}
		};
	}
}

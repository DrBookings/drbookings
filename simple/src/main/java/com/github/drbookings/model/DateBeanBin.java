package com.github.drbookings.model;

import java.util.function.Function;

import com.github.drbookings.ui.beans.DateBean;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DateBeanBin<T> {

	private final int binSize;

	private final ObservableList<DateBean> dateBeans = FXCollections.observableArrayList();

	private final ListProperty<T> values = new SimpleListProperty<>();

	private final Function<DateBean, T> transformer;

	public DateBeanBin(final int binSize, final Function<DateBean, T> transformer) {
		this.binSize = binSize;
		this.transformer = transformer;

	}

	public void add(final DateBean dateBean) {
		if (dateBeans.size() % binSize == 0) {
			for (final DateBean db : dateBeans.subList(dateBeans.size() - 1 - binSize, dateBeans.size())) {
				values.add(transformer.apply(db));
			}
		}
		this.dateBeans.add(dateBean);
	}

	public final ListProperty<T> valuesProperty() {
		return this.values;
	}

	public final ObservableList<T> getValues() {
		return this.valuesProperty().get();
	}

	protected final void setValues(final ObservableList<T> values) {
		this.valuesProperty().set(values);
	}

}

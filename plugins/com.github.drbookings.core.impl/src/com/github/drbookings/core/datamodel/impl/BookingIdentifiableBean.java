package com.github.drbookings.core.datamodel.impl;

import java.util.Collection;
import java.util.List;

import com.github.drbookings.core.datamodel.api.BookingIdentifiable;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

public class BookingIdentifiableBean implements BookingIdentifiable {

	private final ObservableList<String> guestNames;

	private final SimpleObjectProperty<List<String>> guestNamesValue;

	private final SimpleStringProperty id;

	private final SimpleStringProperty status;

	public BookingIdentifiableBean(final ObservableValue<String> id) {
		super();
		this.guestNames = FXCollections.observableArrayList();
		this.status = new SimpleStringProperty();
		this.id = new SimpleStringProperty();
		this.id.bind(id);
		this.guestNamesValue = new SimpleObjectProperty<>(guestNames);
		this.guestNames.addListener((ListChangeListener) c -> guestNamesValue.setValue(guestNames));
	}

	public BookingIdentifiableBean(final String id) {
		this(new SimpleStringProperty(id));
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof BookingIdentifiableBean)) {
			return false;
		}
		final BookingIdentifiableBean other = (BookingIdentifiableBean) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	@Override
	public ObservableList<String> getGuestNames() {
		return guestNames;
	}

	@Override
	public ObservableValue<List<String>> getGuestNamesValue() {
		return guestNamesValue;
	}

	@Override
	public ObservableValue<String> getId() {
		return id;
	}

	@Override
	public SimpleStringProperty getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setGuestNames(final Collection<? extends String> guestNames) {
		this.guestNames.setAll(guestNames);
	}

	public BookingIdentifiableBean setStatus(final String newStatus) {
		this.status.setValue(newStatus);
		return this;
	}

	@Override
	public String toString() {
		return id + "\n" + status.getValue() + "\n" + guestNames.toString();
	}

}

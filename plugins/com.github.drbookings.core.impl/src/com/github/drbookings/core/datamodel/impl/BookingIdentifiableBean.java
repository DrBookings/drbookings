package com.github.drbookings.core.datamodel.impl;

import java.util.Collection;

import com.github.drbookings.core.datamodel.api.BookingIdentifiable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class BookingIdentifiableBean implements BookingIdentifiable {

	private ObservableList<String> guestNames;

	private final String id;

	public BookingIdentifiableBean(final String id) {
		super();
		this.id = id;
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
	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public void setGuestNames(final Collection<? extends String> guestNames) {
		this.guestNames = FXCollections.observableArrayList(guestNames);
	}

	@Override
	public String toString() {
		return id + ", " + guestNames.toString();
	}

}

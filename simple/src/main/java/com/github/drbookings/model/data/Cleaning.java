package com.github.drbookings.model.data;

import java.util.Objects;

public class Cleaning extends Named {

	public Cleaning(final String name) {
		super(name);

	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Cleaning)) {
			return false;
		}
		final Cleaning other = (Cleaning) obj;
		return Objects.equals(this.getName(), other.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(getName());
	}

}

package com.github.drbookings.model.data;

import java.util.Objects;
import java.util.UUID;

public class IDed {

	private final String id;

	public IDed() {
		this(null);
	}

	public IDed(final String id) {
		if (id == null) {
			this.id = UUID.randomUUID().toString();
		} else {
			this.id = id;
		}
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof IDed)) {
			return false;
		}
		final IDed other = (IDed) obj;
		return Objects.equals(this.getId(), other.getId());
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId());
	}
}

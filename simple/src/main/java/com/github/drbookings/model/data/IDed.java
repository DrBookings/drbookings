package com.github.drbookings.model.data;

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
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (id == null ? 0 : id.hashCode());
		return result;
	}
}

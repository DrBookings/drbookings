package com.github.drbookings.model.data;

import java.util.UUID;

public class IDed {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (id == null ? 0 : id.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
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

    private final String id;

    public IDed() {
	this.id = UUID.randomUUID().toString();
    }

    public String getId() {
	return id;
    }
}

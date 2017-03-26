package com.github.drbookings.model.data;

public class Named extends IDed {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = super.hashCode();
	result = prime * result + (name == null ? 0 : name.hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	if (!super.equals(obj)) {
	    return false;
	}
	if (!(obj instanceof Named)) {
	    return false;
	}
	final Named other = (Named) obj;
	if (name == null) {
	    if (other.name != null) {
		return false;
	    }
	} else if (!name.equals(other.name)) {
	    return false;
	}
	return true;
    }

    private String name;

    public Named(final String name) {
	super();

	this.name = name;
    }

    public String getName() {
	return name;
    }

    public void setName(final String name) {
	this.name = name;
    }

}

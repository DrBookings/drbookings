package com.github.drbookings.model.data;

public class Cleaning extends Named {

    @Override
    public int hashCode() {
	final int prime = 31;
	final int result = prime + (getName() == null ? 0 : getName().hashCode());
	return result;
    }

    @Override
    public boolean equals(final Object obj) {
	if (this == obj) {
	    return true;
	}
	// ist das OK!?
	// if (!super.equals(obj)) {
	// return false;
	// }
	if (!(obj instanceof Cleaning)) {
	    return false;
	}
	final Cleaning other = (Cleaning) obj;
	if (getName() == null) {
	    if (other.getName() != null) {
		return false;
	    }
	} else if (!getName().equals(other.getName())) {
	    return false;
	}
	return true;
    }

    public Cleaning(final String name) {
	super(name);

    }

}

package com.github.drbookings.model.data.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.github.drbookings.model.data.Named;

public abstract class NamedProvider<T extends Named> {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (elements == null ? 0 : elements.hashCode());
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
	if (!(obj instanceof NamedProvider)) {
	    return false;
	}
	final NamedProvider other = (NamedProvider) obj;
	if (elements == null) {
	    if (other.elements != null) {
		return false;
	    }
	} else if (!elements.equals(other.elements)) {
	    return false;
	}
	return true;
    }

    private final List<T> elements = new ArrayList<>(); // we cannot use a map
							// name->element here,
							// since the name field
							// is mutable.

    protected abstract T buildNewElement(String name);

    protected List<T> getElementsByName(final String name) {
	return Collections
		.unmodifiableList(elements.stream().filter(e -> e.getName().equals(name)).collect(Collectors.toList()));
    }

    public synchronized T addElement(final String name) {
	// if (name == null || name.length() < 1) {
	// throw new IllegalArgumentException("No name given");
	// }
	final List<T> elements = getElementsByName(name);
	T element;
	if (elements.isEmpty()) {
	    element = buildNewElement(name);
	    this.elements.add(element);
	    return element;
	}
	throw new IllegalArgumentException("Element " + name + " already registerd");
    }

    public synchronized T getElement(final String name) {
	final List<T> elements = getElementsByName(name);
	if (elements.isEmpty()) {
	    return null;
	}
	return elements.get(0);
    }

    public synchronized Collection<T> getElements() {
	return Collections.unmodifiableCollection(elements);
    }

    public synchronized T getOrCreateElement(final String name) {
	T element = getElement(name);
	if (element == null) {
	    element = addElement(name);
	}
	return element;
    }

    public synchronized boolean hasElement(final String name) {
	return !getElementsByName(name).isEmpty();

    }

}

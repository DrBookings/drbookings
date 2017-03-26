package com.github.drbookings.model.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.drbookings.model.data.Named;

public abstract class NamedManager<T extends Named> {

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
	final List<T> elements = getElementsByName(name);
	T element;
	if (elements.isEmpty()) {
	    element = buildNewElement(name);
	    this.elements.add(element);
	    return element;
	}
	throw new IllegalArgumentException("Element " + name + " already registerd");
    }

    public synchronized Optional<T> getElement(final String name) {
	final List<T> elements = getElementsByName(name);
	if (elements.isEmpty()) {
	    return Optional.empty();
	}
	return Optional.of(elements.get(0));
    }

    public synchronized Collection<T> getElements() {
	return Collections.unmodifiableCollection(elements);
    }

    public synchronized T getOrCreateElement(final String name) {
	final List<T> elements = getElementsByName(name);
	T element;
	if (elements.isEmpty()) {
	    element = addElement(name);
	} else {
	    element = elements.get(0);
	}
	return element;
    }

    public synchronized boolean hasElement(final String name) {
	return !getElementsByName(name).isEmpty();

    }

}

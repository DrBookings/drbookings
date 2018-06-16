/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.model.data.manager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.Named;

public abstract class NamedProvider<T extends Named> {

    private static final Logger logger = LoggerFactory.getLogger(NamedProvider.class);

    private final List<T> elements = new ArrayList<>(); // we cannot use a map
    // name->element here,
    // since the name field
    // is mutable.

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
	throw new IllegalArgumentException("Element " + name + " already registered");
    }

    protected abstract T buildNewElement(String name);

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

    public synchronized T getElement(final String name) {
	final List<T> elements = getElementsByName(name);
	if (elements.isEmpty()) {
	    return null;
	}
	if (elements.size() != 1) {
	    if (logger.isErrorEnabled()) {
		logger.error(name + " not unique (" + elements.size() + " elements)");
	    }
	}
	return elements.get(0);
    }

    public synchronized Collection<T> getElements() {
	return Collections.unmodifiableCollection(elements);
    }

    protected List<T> getElementsByName(final String name) {
	return Collections
		.unmodifiableList(elements.stream().filter(e -> e.getName().equals(name)).collect(Collectors.toList()));
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

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = (prime * result) + (elements == null ? 0 : elements.hashCode());
	return result;
    }

}

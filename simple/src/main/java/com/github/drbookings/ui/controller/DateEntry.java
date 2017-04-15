package com.github.drbookings.ui.controller;

import java.time.LocalDate;

public class DateEntry<E> {

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (e == null ? 0 : e.hashCode());
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
	if (!(obj instanceof DateEntry)) {
	    return false;
	}
	final DateEntry other = (DateEntry) obj;
	if (e == null) {
	    if (other.e != null) {
		return false;
	    }
	} else if (!e.equals(other.e)) {
	    return false;
	}
	return true;
    }

    private final LocalDate date;

    private final E e;

    public DateEntry(final LocalDate date, final E element) {
	super();
	this.date = date;
	this.e = element;
    }

    public LocalDate getDate() {
	return date;
    }

    public E getElement() {
	return e;
    }

    @Override
    public String toString() {
	return e.toString();
    }

}

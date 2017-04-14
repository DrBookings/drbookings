package com.github.drbookings.ui.controller;

import java.time.LocalDate;

public class DateEntry<E> {

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

package com.github.drbookings.model.data;

import java.time.LocalDate;

public interface DateEntry<E> extends Comparable<DateEntry<E>> {

    @Override
    default int compareTo(final DateEntry<E> o) {
	return getDate().compareTo(o.getDate());
    }

    LocalDate getDate();

    E getElement();

}
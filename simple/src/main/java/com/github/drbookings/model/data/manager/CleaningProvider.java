package com.github.drbookings.model.data.manager;

import com.github.drbookings.model.data.Cleaning;

public class CleaningProvider extends NamedProvider<Cleaning> {

    @Override
    protected Cleaning buildNewElement(final String name) {
	return new Cleaning(name);
    }

}

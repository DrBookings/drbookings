package com.github.drbookings.model;

import java.util.Collection;
import java.util.function.Function;

public class EarningsSum implements Function<Collection<? extends EarningsProvider>, Number> {

    private final boolean netEarnings;

    public EarningsSum(final boolean netEarnings) {
	super();
	this.netEarnings = netEarnings;
    }

    @Override
    public Number apply(final Collection<? extends EarningsProvider> t) {
	return t.stream().mapToDouble(b -> b.getEarnings(netEarnings)).sum();
    }

}

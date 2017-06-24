package com.github.drbookings.model;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class PayoutCollector implements Collector<Payout, Payout, Payout> {

	@Override
	public Supplier<Payout> supplier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BiConsumer<Payout, Payout> accumulator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BinaryOperator<Payout> combiner() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Function<Payout, Payout> finisher() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.stream.Collector.Characteristics> characteristics() {
		// TODO Auto-generated method stub
		return null;
	}

}

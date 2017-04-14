package com.github.drbookings;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.Callable;

import org.junit.Test;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SimpleTest {

    private final DoubleProperty allEarnings = new SimpleDoubleProperty();

    private final DoubleProperty dailyEarnings = new SimpleDoubleProperty();

    public SimpleTest() {
	dailyEarningsProperty().bind(Bindings.createObjectBinding(calculateDailyEarinings(), allEarningsProperty()));
    }

    private Callable<Number> calculateDailyEarinings() {
	return () -> {
	    return getAllEarnings() / 4;
	};
    }

    @Test
    public void test() {
	final SimpleTest t = new SimpleTest();
	t.setAllEarnings(40);
	assertEquals(10, t.getDailyEarnings(), 0);

    }

    public DoubleProperty allEarningsProperty() {
	return this.allEarnings;
    }

    public double getAllEarnings() {
	return this.allEarningsProperty().get();
    }

    public void setAllEarnings(final double allEarnings) {
	this.allEarningsProperty().set(allEarnings);
    }

    public DoubleProperty dailyEarningsProperty() {
	return this.dailyEarnings;
    }

    public double getDailyEarnings() {
	return this.dailyEarningsProperty().get();
    }

    public void setDailyEarnings(final double dailyEarnings) {
	this.dailyEarningsProperty().set(dailyEarnings);
    }

}

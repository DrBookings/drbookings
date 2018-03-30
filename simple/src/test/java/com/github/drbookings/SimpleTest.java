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

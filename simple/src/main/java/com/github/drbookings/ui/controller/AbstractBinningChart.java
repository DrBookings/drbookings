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

package com.github.drbookings.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public abstract class AbstractBinningChart<T> {

	public final static int DEFAULT_BIN_SIZE = 1;
	protected final List<T> bin = new ArrayList<>();
	private final DoubleProperty binSize = new SimpleDoubleProperty(DEFAULT_BIN_SIZE);
	protected final Set<String> categories = new LinkedHashSet<>();

	private XYChart<String, Number> chart;

	public XYChart<String, Number> getChart() {
		return chart;
	}

	public void setChart(final XYChart<String, Number> chart) {
		this.chart = chart;
	}

	public CategoryAxis getxAxis() {
		return xAxis;
	}

	public void setxAxis(final CategoryAxis xAxis) {
		this.xAxis = xAxis;
	}

	Map<String, Series<String, Number>> mapSeries = new HashMap<>();

	private CategoryAxis xAxis;

	public final DoubleProperty binSizeProperty() {
		return this.binSize;
	}

	protected abstract void flushBin();

	public final int getBinSize() {
		return (int) this.binSizeProperty().get();
	}

	public final void setBinSize(final int binSize) {
		this.binSizeProperty().set(binSize);
	}
}

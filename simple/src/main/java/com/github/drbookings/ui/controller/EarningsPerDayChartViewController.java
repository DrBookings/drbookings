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

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.BookingEntry;
import com.github.drbookings.SettingsManager;
import com.github.drbookings.model.BinType;
import com.github.drbookings.ui.selection.BookingSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import net.sf.kerner.utils.exception.ExceptionUnknownType;

public class EarningsPerDayChartViewController extends AbstractBinningChart<BookingEntry> implements Initializable {

    private class UpdateUIListener implements ChangeListener<Object> {
	@Override
	public void changed(final ObservableValue<? extends Object> observable, final Object oldValue,
		final Object newValue) {
	    if ((oldValue instanceof Number) && (newValue instanceof Number)) {
		final int o = ((Number) oldValue).intValue();
		final int n = ((Number) newValue).intValue();
		if (o == n) {
		    return;
		}
	    }
	    doChart(BookingSelectionManager.getInstance().selectionProperty());
	}
    }

    private final static Logger logger = LoggerFactory.getLogger(EarningsPerDayChartViewController.class);

    @FXML
    private XYChart<String, Number> chart;

    @FXML
    private Slider slider;

    @FXML
    private Label sliderValue;

    @FXML
    private ComboBox<BinType> toggle;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;

    public EarningsPerDayChartViewController() {

    }

    private Callable<String> createSliderLabel() {
	return () -> {
	    switch (toggle.getSelectionModel().selectedItemProperty().get()) {
	    case MEAN:
		return "Average over Days";
	    case SUM:
		return "Sum over Days:";
	    default:
		throw new ExceptionUnknownType(toggle.getSelectionModel().getSelectedItem());
	    }
	};
    }

    protected void doChart() {
	doChart(BookingSelectionManager.getInstance().selectionProperty());

    }

    protected void doChart(final List<? extends BookingEntry> allElements) {

	categories.clear();
	mapSeries.clear();
	chart.getData().clear();
	// final int size = allElements.stream().map(e ->
	// e.getDate()).collect(Collectors.toSet()).size();

	for (int i = 0; i < allElements.size(); i++) {
	    final BookingEntry db = allElements.get(i);
	    bin.add(db);
	    if (bin.stream().map(e -> e.getDate()).collect(Collectors.toSet()).size() >= getBinSize()) {
		flushBin();
	    }
	}
	flushBin();

	final ObservableList<String> c = FXCollections.observableArrayList(categories);
	Collections.sort(c);
	xAxis.setCategories(c);
	xAxis.setAutoRanging(true);

    }

    @Override
    protected void flushBin() {
	if (bin.isEmpty()) {
	    return;
	}
	final LocalDate xValue = bin.stream().map(d -> d.getDate()).max((d1, d2) -> d1.compareTo(d2)).get();
	final Map<String, Number> yValue = new TreeMap<>();
	final int dayCnt = bin.stream().map(d -> d.getDate()).collect(Collectors.toSet()).size();

	for (final BookingEntry d : bin) {
	    final String originName = d.getElement().getBookingOrigin().getName();

	    Number n = yValue.getOrDefault(originName, 0);
	    n = n.doubleValue() + d.getEarnings(SettingsManager.getInstance().isShowNetEarnings());
	    yValue.put(originName, n);
	}

	for (final Entry<String, Number> e : yValue.entrySet()) {
	    Series<String, Number> s = mapSeries.get(e.getKey());
	    if (s == null) {
		s = new Series<>();
		s.setName(e.getKey());
		mapSeries.put(e.getKey(), s);
		chart.getData().add(s);
	    }
	    Number value = e.getValue();
	    if (BinType.MEAN.equals(toggle.getSelectionModel().getSelectedItem())) {
		if (dayCnt < 1) {
		    if (logger.isWarnEnabled()) {
			logger.warn("Invalid data, day cnt: " + dayCnt + " for "
				+ bin.stream().map(d -> d.getDate()).collect(Collectors.toSet()));
		    }
		}
		value = value.doubleValue() / dayCnt;
	    }
	    final XYChart.Data<String, Number> data = new XYChart.Data<>(xValue.toString(), value);
	    categories.add(xValue.toString());
	    s.getData().add(data);
	}
	if (logger.isDebugEnabled()) {
	    logger.debug(xValue + ": " + yValue);
	}

	bin.clear();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	toggle.setItems(FXCollections.observableArrayList(BinType.MEAN, BinType.SUM));
	toggle.getSelectionModel().select(0);
	toggle.getSelectionModel().selectedItemProperty().addListener(c -> doChart());
	sliderValue.textProperty().bind(Bindings.createStringBinding(
		() -> Integer.valueOf((int) slider.getValue()).toString(), slider.valueProperty()));
	binSizeProperty().bind(slider.valueProperty());
	slider.valueProperty().addListener(new UpdateUIListener());
	SettingsManager.getInstance().additionalCostsProperty().addListener(new UpdateUIListener());
	SettingsManager.getInstance().referenceColdRentLongTermProperty().addListener(new UpdateUIListener());
	SettingsManager.getInstance().showNetEarningsProperty().addListener(new UpdateUIListener());
	BookingSelectionManager.getInstance().selectionProperty()
		.addListener((ListChangeListener<BookingEntry>) c -> doChart());

	setChart(chart);
	setxAxis(xAxis);
	doChart();

    }

    public void shutDown() {

    }

}

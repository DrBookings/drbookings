package com.github.drbookings.ui.controller;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

import com.github.drbookings.model.BinType;
import com.github.drbookings.model.NightlyRateView;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.selection.BookingSelectionManager;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class NightlyRateChartViewController implements Initializable {

    static void checkNoData(final XYChart.Series<?, Number> series) {
        double d1 = 0;
        XYChart.Data<?, Number> last = null;
        for (final Object data : series.getData()) {
            if (data instanceof XYChart.Data<?, ?>) {
                final XYChart.Data<?, Number> cdata = (XYChart.Data<?, Number>) data;
                if (last != null && (last.getYValue() == null || last.getYValue().doubleValue() == 0)) {
                    final double mid = (d1 + cdata.getYValue().doubleValue()) / 2;
                    last.setYValue(mid);
                }
                if (last != null) {
                    d1 = last.getYValue().doubleValue();
                }
                last = cdata;
            }
        }
        if (last != null && (last.getYValue() == null || last.getYValue().doubleValue() == 0)) {
            last.setYValue(d1);
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(NightlyRateChartViewController.class);

    private class UpdateUIListener implements ChangeListener<Object> {
        @Override
        public void changed(final ObservableValue<? extends Object> observable, final Object oldValue,
                            final Object newValue) {
            if (oldValue instanceof Number && newValue instanceof Number) {
                final int o = ((Number) oldValue).intValue();
                final int n = ((Number) newValue).intValue();
                if (o == n) {
                    return;
                }
            }
            doChart(BookingSelectionManager.getInstance().selectionProperty());
        }
    }

    @FXML
    private XYChart<String, Number> chart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;


    @FXML
    private Slider slider;

    @FXML
    private Label sliderValue;

    @FXML
    private ComboBox<BinType> toggle;

    private final ObservableList<XYChart.Series<String, Number>> chartSeries = FXCollections.observableArrayList();

    private final Map<BookingOrigin, XYChart.Series<String, Number>> seriesMap = new LinkedHashMap<>();


    public NightlyRateChartViewController() {

    }

    public final static int DEFAULT_BIN_SIZE = 1;
    protected final List<T> bin = new ArrayList<>();
    private final DoubleProperty binSize = new SimpleDoubleProperty(DEFAULT_BIN_SIZE);

    public double getBinSize() {
        return binSize.get();
    }

    public DoubleProperty binSizeProperty() {
        return binSize;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        chart.setData(chartSeries);
        SettingsManager.getInstance().showNetEarningsProperty().addListener(new UpdateUIListener());
        BookingSelectionManager.getInstance().selectionProperty()
                .addListener((ListChangeListener<BookingEntry>) c -> doChart());
        toggle.setItems(FXCollections.observableArrayList(BinType.MEAN, BinType.SUM));
        toggle.getSelectionModel().select(0);
        toggle.getSelectionModel().selectedItemProperty().addListener(c -> doChart());
        sliderValue.textProperty().bind(Bindings.createStringBinding(
                () -> Integer.valueOf((int) slider.getValue()).toString(), slider.valueProperty()));
        binSizeProperty().bind(slider.valueProperty());
        slider.valueProperty().addListener(new UpdateUIListener());
        doChart();

    }

    protected void doChart() {
        doChart(BookingSelectionManager.getInstance().selectionProperty());

    }

    protected void doChart(final List<? extends BookingEntry> allElements) {
        if (logger.isDebugEnabled()) {
            logger.debug("Drawing chart");
        }
        chartSeries.clear();
        seriesMap.clear();

        final NightlyRateView view = new NightlyRateView(allElements);
        view.setBinSize(getBinSize());
        view.setBinType(toggle.getSelectionModel().getSelectedItem());

        for (final Map.Entry<BookingOrigin, Map<LocalDate, Number>> e : view.getData().entrySet()) {
            XYChart.Series<String, Number> series = seriesMap.get(e.getKey());
            if (series == null) {
                series = new XYChart.Series<>(e.getKey().getName(), FXCollections
                        .observableArrayList
                                ());
                if (logger.isDebugEnabled()) {
                    logger.debug("Adding new series " + series);
                }
                seriesMap.put(e.getKey(), series);
                chartSeries.add(series);
            }
            for (final Map.Entry<LocalDate, Number> e2 : e.getValue().entrySet()) {
                series.getData().add(new XYChart.Data<>(e2.getKey().toString(), e2.getValue()));
            }
            //checkNoData(series);
        }
    }


    public void shutDown() {

    }

}

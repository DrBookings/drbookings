/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.BookingEntryBin;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.selection.BookingSelectionManager;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.YearMonth;
import java.util.ResourceBundle;

public class EarningsViewController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(EarningsViewController.class);
    @FXML
    XYChart<String, Number> chart;
    private MainManager manager;

    public MainManager getManager() {
        return manager;
    }

    public void setManager(final MainManager manager) {
        this.manager = manager;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        BookingSelectionManager.getInstance().selectionProperty()
                .addListener((ListChangeListener<BookingEntry>) c -> doChart(c.getList()));
    }

    private void doChart(ObservableList<? extends BookingEntry> allElements) {
        if (logger.isDebugEnabled()) {
            logger.debug("Doing chart");
        }
        boolean netEarnings = SettingsManager.getInstance().isShowNetEarnings();
        chart.getData().clear();
        XYChart.Series<String, Number> s = new XYChart.Series<String, Number>();
        s.setName("test");
        BookingEntryMonthBins bin = new BookingEntryMonthBins(allElements);
        for (BookingEntryBin<YearMonth> be : bin.getBins()) {
            double sum = be.getEntries().stream().mapToDouble(e -> e.getEarnings(netEarnings)).sum();
            final XYChart.Data<String, Number> data = new XYChart.Data<>(be.getLabel().toString(), sum);
            s.getData().add(data);
        }
        chart.getData().add(s);

    }

}

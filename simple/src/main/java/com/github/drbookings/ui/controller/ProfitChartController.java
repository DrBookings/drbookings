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

import java.net.URL;
import java.time.LocalDate;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.selection.BookingSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class ProfitChartController extends AbstractBinningChart<DateBean> implements Initializable {

	private final static Logger logger = LoggerFactory.getLogger(ProfitChartController.class);

	@FXML
	private StackedBarChart<String, Number> chart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private final DoubleProperty allBookingEntries = new SimpleDoubleProperty();

	public ProfitChartController() {

	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		allBookingEntriesProperty().bind(Bindings.createDoubleBinding(calculateAllBookingEntries(),
				BookingSelectionManager.getInstance().selectionProperty()));
	}

	private Callable<Double> calculateAllBookingEntries() {
		return () -> Double.valueOf(BookingSelectionManager.getInstance().getSelection().size());
	}

	@Override
	protected void flushBin() {
		if (bin.isEmpty()) {
			return;
		}
		final Optional<LocalDate> xValue = bin.stream().map(d -> d.getDate()).max((d1, d2) -> d1.compareTo(d2));
		final Map<String, Number> yValue = new TreeMap<>();

		final double costsToCover = getCostsToCover();
		final double refColdRentLongTerm = getRefColdRentLongTerm();
		for (final DateBean d : bin) {
			for (final Entry<String, Number> e : d.getEarningsPerOrigin().entrySet()) {
				double n = yValue.getOrDefault(e.getKey(), Double.valueOf(0)).doubleValue();
				final double earnings = e.getValue().doubleValue();
				final double sizeThis = 1;
				final double sizeAll = allBookingEntries.get();
				System.err.println(sizeThis);
				System.err.println(sizeAll);
				final double percent = sizeThis / sizeAll;
				if (logger.isDebugEnabled()) {
					logger.debug("percent: " + percent);
					logger.debug("earnings: " + earnings);
					logger.debug("costsToCover: " + (costsToCover * percent));
					logger.debug("refColdRent: " + (refColdRentLongTerm * percent));
				}
				final double performance = (earnings - (costsToCover * percent) - (refColdRentLongTerm * percent));
				n += performance / bin.size();
				yValue.put(e.getKey(), n);
				System.err.println("yValue: " + yValue);

			}
		}

		for (final Entry<String, Number> e : yValue.entrySet()) {
			// System.err.println("Entry " + e);
			Series<String, Number> s = mapSeries.get(e.getKey());
			if (s == null) {
				s = new Series<>();
				s.setName(e.getKey());
				mapSeries.put(e.getKey(), s);
				chart.getData().add(s);

			}
			final XYChart.Data<String, Number> data = new XYChart.Data<>(xValue.get().toString(), e.getValue());
			categories.add(xValue.get().toString());
			s.getData().add(data);
			// System.err.println("Adding " + data);
		}
		bin.clear();
	}

	private static double getCostsToCover() {
		double result = 0;
		result += SettingsManager.getInstance().getAdditionalCosts();
		result *= SettingsManager.getInstance().getNumberOfRooms();
		return result;
	}

	private static double getRefColdRentLongTerm() {
		return SettingsManager.getInstance().getReferenceColdRentLongTerm()
				* SettingsManager.getInstance().getNumberOfRooms();
	}

	public final DoubleProperty allBookingEntriesProperty() {
		return this.allBookingEntries;
	}

	public final double getAllBookingEntries() {
		return this.allBookingEntriesProperty().get();
	}

	public final void setAllBookingEntries(final double allBookingEntries) {
		this.allBookingEntriesProperty().set(allBookingEntries);
	}

}

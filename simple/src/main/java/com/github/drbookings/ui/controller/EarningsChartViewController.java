package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.beans.DateBean;

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
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Slider;

public class EarningsChartViewController implements Initializable {

	private class DateBeanListener implements ListChangeListener<DateBean> {

		@Override
		public void onChanged(final javafx.collections.ListChangeListener.Change<? extends DateBean> c) {
			while (c.next()) {

			}
			doChart(c.getList());
		}

	}

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(EarningsChartViewController.class);

	public final static int DEFAULT_BIN_SIZE = 1;

	public final static int DEFAULT_MONTH_LOOKBACK = 1;

	private MainManager mainManager;

	@FXML
	private StackedBarChart<String, Number> chart;

	@FXML
	private Slider slider;

	@FXML
	private Slider slider2;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	private final DoubleProperty binSize = new SimpleDoubleProperty(DEFAULT_BIN_SIZE);

	private final DoubleProperty monthLookBack = new SimpleDoubleProperty(DEFAULT_MONTH_LOOKBACK);

	private final List<DateBean> bin = new ArrayList<>();

	public EarningsChartViewController() {

	}

	private final Set<String> categories = new LinkedHashSet<>();

	private void doChart(final List<? extends DateBean> allElements) {

		categories.clear();
		mapSeries.clear();
		chart.getData().clear();

		for (int i = 0; i < allElements.size(); i++) {
			final DateBean db = allElements.get(i);
			if (db.getDate().isAfter(LocalDate.now().minusMonths(getMonthLookBack()))
					&& db.getDate().isBefore(LocalDate.now())) {
				bin.add(db);
				if (bin.size() >= getBinSize()) {
					flushBin();
				}
			}
		}
		flushBin();
		// for (final Series<String, Number> s : chart.getData()) {
		// Collections.sort(s.getData(), (o1, o2) -> {
		// return o1.getXValue().compareTo(o2.getXValue());
		// });
		// }
		// chart.getData().sort((o1, o2) ->
		// o1.getName().compareTo(o2.getName()));
		final ObservableList<String> c = FXCollections.observableArrayList(categories);
		Collections.sort(c);
		xAxis.setCategories(c);
		xAxis.setAutoRanging(true);

	}

	public final DoubleProperty binSizeProperty() {
		return this.binSize;
	}

	Map<String, Series<String, Number>> mapSeries = new HashMap<>();

	private void flushBin() {
		if (bin.isEmpty()) {
			return;
		}
		final Optional<LocalDate> xValue = bin.stream().map(d -> d.getDate()).max((d1, d2) -> d1.compareTo(d2));
		final Map<String, Number> yValue = new TreeMap<>();

		for (final DateBean d : bin) {
			for (final Entry<String, Number> e : d.getEarningsPerOrigin().entrySet()) {
				double n = yValue.getOrDefault(e.getKey(), Double.valueOf(0)).doubleValue();
				n += e.getValue().doubleValue() / bin.size();
				yValue.put(e.getKey(), n);

			}
		}

		for (final Entry<String, Number> e : yValue.entrySet()) {
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
		}
		bin.clear();
	}

	public final int getBinSize() {
		return (int) this.binSizeProperty().get();
	}

	public MainManager getMainManager() {
		return mainManager;
	}

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
			doChart(mainManager.getUIData());
		}
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		binSizeProperty().bind(slider.valueProperty());
		monthLookBackProperty().bind(slider2.valueProperty());
		slider.valueProperty().addListener(new UpdateUIListener());
		slider2.valueProperty().addListener(new UpdateUIListener());
		SettingsManager.getInstance().additionalCostsProperty().addListener(new UpdateUIListener());
		SettingsManager.getInstance().referenceColdRentLongTermProperty().addListener(new UpdateUIListener());
		SettingsManager.getInstance().showNetEarningsProperty().addListener(new UpdateUIListener());

	}

	public final void setBinSize(final int binSize) {
		this.binSizeProperty().set(binSize);
	}

	public void setMainManager(final MainManager mainManager) {
		this.mainManager = mainManager;
		this.mainManager.getUIData().addListener(new DateBeanListener());
		doChart(mainManager.getUIData());

	}

	public void shutDown() {

	}

	public final DoubleProperty monthLookBackProperty() {
		return this.monthLookBack;
	}

	public final int getMonthLookBack() {
		return (int) this.monthLookBackProperty().get();
	}

	public final void setMonthLookBack(final int monthLookBack) {
		this.monthLookBackProperty().set(monthLookBack);
	}

}

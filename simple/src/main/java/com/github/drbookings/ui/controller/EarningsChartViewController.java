package com.github.drbookings.ui.controller;

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

import com.github.drbookings.model.BinType;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.selection.DateBeanSelectionManager;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import net.sf.kerner.utils.exception.ExceptionUnknownType;

public class EarningsChartViewController extends AbstractBinningChart<DateBean> implements Initializable {

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(EarningsChartViewController.class);

	@FXML
	private StackedBarChart<String, Number> chart;

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

	public EarningsChartViewController() {

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
			doChart(DateBeanSelectionManager.getInstance().selectionProperty());
		}
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
		DateBeanSelectionManager.getInstance().selectionProperty()
				.addListener((ListChangeListener<DateBean>) c -> doChart());

		setChart(chart);
		setxAxis(xAxis);
		doChart();

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
		doChart(DateBeanSelectionManager.getInstance().selectionProperty());

	}

	@Override
	protected void flushBin() {
		if (bin.isEmpty()) {
			return;
		}
		final Optional<LocalDate> xValue = bin.stream().map(d -> d.getDate()).max((d1, d2) -> d1.compareTo(d2));
		final Map<String, Number> yValue = new TreeMap<>();

		for (final DateBean d : bin) {
			// System.err.println("now " + d);
			for (final Entry<String, Number> e : d.getEarningsPerOrigin().entrySet()) {
				double n = yValue.getOrDefault(e.getKey(), Double.valueOf(0)).doubleValue();
				if (BinType.MEAN.equals(toggle.getSelectionModel().getSelectedItem())) {
					n += e.getValue().doubleValue() / bin.size();
				} else if (BinType.SUM.equals(toggle.getSelectionModel().getSelectedItem())) {
					n += e.getValue().doubleValue();
				}

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

	public void shutDown() {

	}

}

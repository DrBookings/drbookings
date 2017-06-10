package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BinYearMonth;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.beans.DateBean;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class OverviewChartController implements Initializable {

	private final static Logger logger = LoggerFactory.getLogger(OverviewChartController.class);

	private MainController mainController;

	@FXML
	private StackedBarChart<String, Number> chart;

	@FXML
	private CategoryAxis xAxis;

	@FXML
	private NumberAxis yAxis;

	// @FXML
	// private Label labelStartDate;
	//
	// @FXML
	// private Label labelEndDate;

	@FXML
	private HBox chart2;

	private final BinYearMonth bin = new BinYearMonth();

	private boolean expanded = false;

	public OverviewChartController() {

	}

	private double getCostsToCover() {
		double result = 0;
		result += SettingsManager.getInstance().getAdditionalCosts();
		result *= SettingsManager.getInstance().getNumberOfRooms();
		return result;
	}

	public MainController getMainController() {
		return mainController;
	}

	private double getRefColdRentLongTerm() {
		return SettingsManager.getInstance().getReferenceColdRentLongTerm()
				* SettingsManager.getInstance().getNumberOfRooms();
	}

	private void handleChartClickEvent(final MouseEvent event) {
		final Set<String> categories = new LinkedHashSet<>();
		final Map<BookingOrigin, ObservableList<XYChart.Data<String, Number>>> chartData = new HashMap<>();

		int max = 0;
		final double costsToCover = getCostsToCover();
		final double refColdRentLongTerm = getRefColdRentLongTerm();
		for (final Entry<YearMonth, Collection<DateBean>> e : bin.getYearMonth2DateBeanMap().entrySet()) {

			final BookingsByOrigin bo = new BookingsByOrigin(e.getValue().stream().flatMap(d -> d.getRooms().stream())
					.flatMap(r -> r.getFilteredBookingEntries().stream()).collect(Collectors.toList()));

			final Map<BookingOrigin, Collection<BookingEntry>> map = bo.getMap();

			double totalPerformance = 0;
			for (final Entry<BookingOrigin, Collection<BookingEntry>> entry : map.entrySet()) {
				final double sizeAll = bo.getAllBookings().size();
				final double sizeThis = entry.getValue().size();
				final double percent = sizeThis / sizeAll;
				final double earnings = entry.getValue().stream().mapToDouble(be -> be.getNetEarnings()).sum();
				final double performance = (earnings - (costsToCover * percent) - (refColdRentLongTerm * percent));
				totalPerformance += performance;
				if (earnings <= 0) {
					continue;
				}

				if (logger.isDebugEnabled()) {
					logger.debug(e.getKey() + "(" + String.format("%4.2f", percent) + "): " + entry.getKey() + "->"
							+ String.format("%4.0f", performance));
				}

				final XYChart.Data<String, Number> data = new XYChart.Data<>(e.getKey().toString(), performance);
				categories.add(e.getKey().toString());
				final ObservableList<Data<String, Number>> hans = chartData.getOrDefault(entry.getKey(),
						FXCollections.observableArrayList());
				hans.add(data);
				chartData.put(entry.getKey(), hans);

			}

			if ((int) totalPerformance > max) {
				max = (int) totalPerformance;
			}
		}

		chart.getData().clear();

		chartData.forEach((bo, co) -> {
			final Series s = new Series<>(bo.toString(), co);
			chart.getData().add(s);
		});

		final ObservableList<String> c = FXCollections.observableArrayList(categories);
		Collections.sort(c);
		xAxis.setCategories(c);
		xAxis.setAutoRanging(true);

		yAxis.setUpperBound(max);
		yAxis.setTickUnit(200);
		yAxis.setLowerBound(-400);

		if (expanded) {
			chart.setLegendVisible(true);
			chart.setMinHeight(200);
		} else {
			chart.setLegendVisible(false);
			chart.setMinHeight(60);
		}

		// for (final XYChart.Series<String, Number> series : chart.getData()) {
		// if ("booking".equalsIgnoreCase(series.getName())) {
		// for (final Data data : series.getData()) {
		// data.getNode().setStyle("-fx-background-color:
		// rgba(8.0,152.0,255.0,1);");
		// }
		// } else if ("airbnb".equalsIgnoreCase(series.getName())) {
		// for (final Data data : series.getData()) {
		// data.getNode().setStyle("-fx-background-color:
		// rgba(255.0,90.0,95.0,1);");
		// }
		// }
		// }

		expanded = !expanded;

	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {

		chart.setOnMouseClicked(event -> {
			// if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
			handleChartClickEvent(event);
			// }
		});

	}

	public void setMainController(final MainController mainController) {
		this.mainController = mainController;
		bin.bind(mainController.getManager().getUIData());
	}

	public void shutDown() {

	}

}

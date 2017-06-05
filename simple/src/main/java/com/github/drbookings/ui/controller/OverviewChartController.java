package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.YearMonth;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BinYearMonth;
import com.github.drbookings.model.MoneyMonitor;
import com.github.drbookings.model.ProfitProvider;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.application.Platform;
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
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class OverviewChartController implements Initializable {

    private class PieChartUpdater implements ChangeListener<Object> {
	@Override
	public void changed(final ObservableValue<? extends Object> observable, final Object oldValue,
		final Object newValue) {
	    updateChart2();

	}
    }

    private class PieChartUpdater2 implements ListChangeListener<RoomBean> {

	@Override
	public void onChanged(final javafx.collections.ListChangeListener.Change<? extends RoomBean> c) {
	    updateChart2();

	}
    }

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

    private final ProfitProvider pc = new ProfitProvider();

    public OverviewChartController() {

    }

    private double getCostsToCover() {
	double result = 0;
	result += SettingsManager.getInstance().getAdditionalCosts();
	result *= SettingsManager.getInstance().getNumberOfRooms();
	return result;
    }

    private double getEarnings() {

	return MoneyMonitor.getInstance().getTotalNetEarnings();
    }

    private double getHours() {
	return SettingsManager.getInstance().getWorkHoursPerMonth();
    }

    public MainController getMainController() {
	return mainController;
    }

    private double getRefColdRentLongTerm() {
	return SettingsManager.getInstance().getReferenceColdRentLongTerm()
		* SettingsManager.getInstance().getNumberOfRooms();
    }

    private void handleChartClickEvent(final MouseEvent event) {

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
		// data.nodeProperty().addListener(new
		// ChangeListener<Node>() {
		// @Override
		// public void changed(final ObservableValue<? extends Node>
		// ov,
		// final Node oldNode,
		// final Node newNode) {
		// if (newNode != null) {
		// if (data.getYValue().floatValue() > 0) {
		// newNode.setStyle("-fx-bar-fill: grey;");
		// } else {
		// newNode.setStyle("-fx-bar-fill: tomato;");
		// }
		// }
		// }
		// });
		final ObservableList<Data<String, Number>> hans = chartData.getOrDefault(entry.getKey(),
			FXCollections.observableArrayList());
		hans.add(data);
		chartData.put(entry.getKey(), hans);

	    }

	    if (totalPerformance > max) {
		max = (int) totalPerformance;
	    }
	}

	chart.getData().clear();
	chartData.forEach((bo, co) -> {
	    final Series s = new Series<>(bo.toString(), co);
	    chart.getData().add(s);
	});

	xAxis.getCategories().addAll(chartData.keySet().stream().map(o -> o.toString()).collect(Collectors.toList()));
	chart.setLegendVisible(true);
	yAxis.setUpperBound(max + (max * 0.1));
	yAxis.setTickUnit(200);
	yAxis.setLowerBound(-200);
	chart.setMinHeight(200);

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	chart.setOnMouseClicked(event -> {
	    // if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
	    handleChartClickEvent(event);
	    // }
	});

	chart.getData().addListener(new ListChangeListener<Series<String, Number>>() {

	    @Override
	    public void onChanged(
		    final javafx.collections.ListChangeListener.Change<? extends Series<String, Number>> c) {
		while (c.next()) {
		    for (final Series s : c.getAddedSubList()) {
			if ("booking".equalsIgnoreCase(s.getName())) {
			    if (s.getNode() != null) {
				s.getNode().getStyleClass().add(".source-background-booking");
			    }
			} else if ("airbnb".equalsIgnoreCase(s.getName())) {
			    if (s.getNode() != null) {
				s.getNode().getStyleClass().add(".source-background-airbnb");
			    }
			}
		    }
		}
	    }
	});

	updateChart2();
	SettingsManager.getInstance().additionalCostsProperty().addListener(new PieChartUpdater());
	SettingsManager.getInstance().referenceColdRentLongTermProperty().addListener(new PieChartUpdater());
	SettingsManager.getInstance().numberOfRoomsProperty().addListener(new PieChartUpdater());
	SettingsManager.getInstance().showNetEarningsProperty().addListener(new PieChartUpdater());
	CellSelectionManager.getInstance().getSelection().addListener(new PieChartUpdater2());

    }

    public void setMainController(final MainController mainController) {
	this.mainController = mainController;
	bin.bind(mainController.getManager().getUIData());
    }

    public void shutDown() {

    }

    private void updateChart2() {
	Platform.runLater(() -> updateChart2FX());
    }

    private void updateChart2FX() {
	chart.setMinHeight(60);
	chart.setLegendVisible(false);

	chart2.getChildren().clear();
	final Label l = new Label("Profit total" + String.format("%6.0f€", pc.getProfit()) + "\n" + "Profit/hour"
		+ String.format("%6.0f€", pc.getProfitPerHour()));
	l.setWrapText(true);
	chart2.getChildren().add(l);
    }

}

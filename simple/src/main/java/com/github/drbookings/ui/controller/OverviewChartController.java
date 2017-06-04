package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BinYearMonth;
import com.github.drbookings.model.MoneyMonitor;
import com.github.drbookings.model.settings.SettingsManager;
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
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
    private BarChart<String, Number> chart;

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

    private final ObservableList<XYChart.Data<String, Number>> chartData = FXCollections.observableArrayList();

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

	chartData.clear();

	double max = 0;
	final double costsToCover = getCostsToCover();
	final double refColdRentLongTerm = getRefColdRentLongTerm();
	for (final Entry<YearMonth, Collection<DateBean>> e : bin.getYearMonth2DateBeanMap().entrySet()) {
	    final double earnings = e.getValue().stream().flatMap(d -> d.getRooms().stream())
		    .flatMap(r -> r.getFilteredBookingEntries().stream()).mapToDouble(be -> be.getNetEarnings()).sum();

	    if (earnings > 0) {
		final int profit = (int) (earnings - costsToCover - refColdRentLongTerm);

		if (profit > max) {
		    max = profit;
		}
		if (logger.isDebugEnabled()) {
		    logger.debug(e.getKey() + "->" + profit);
		}

		final XYChart.Data<String, Number> data1 = new XYChart.Data<>(e.getKey().toString(), profit);
		data1.nodeProperty().addListener(new ChangeListener<Node>() {
		    @Override
		    public void changed(final ObservableValue<? extends Node> ov, final Node oldNode,
			    final Node newNode) {
			if (newNode != null) {
			    if (data1.getYValue().floatValue() > 0) {
				newNode.setStyle("-fx-bar-fill: grey;");
			    } else {
				newNode.setStyle("-fx-bar-fill: tomato;");
			    }
			}
		    }
		});
		chartData.add(data1);

	    }

	}
	yAxis.setUpperBound(max);
	yAxis.setTickUnit(500);
	yAxis.setLowerBound(-250);
	chart.setMinHeight(140);

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	chart.setOnMouseClicked(event -> {
	    // if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
	    handleChartClickEvent(event);
	    // }
	});

	chart.getData().add(new XYChart.Series<>("s1", chartData));

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
	final Set<LocalDate> dates = CellSelectionManager.getInstance().getSelection().stream().map(r -> r.getDate())
		.collect(Collectors.toSet());
	if (logger.isDebugEnabled()) {
	    logger.debug(dates.size() + " days selected");
	}
	final double costsToCover = getCostsToCover() / 30d * dates.size();
	final double refColdRentLongTerm = getRefColdRentLongTerm() / 30d * dates.size();
	final double netEarnings = getEarnings();
	final double hours = getHours() / 30d * dates.size();
	final double payment = netEarnings - costsToCover - refColdRentLongTerm;
	final double paymentPerHour = payment / hours;

	if (logger.isDebugEnabled()) {
	    logger.debug(String.format("CostsToCover %8.2f", costsToCover));
	    logger.debug(String.format("CostsToCover plus RefRent %8.2f", (costsToCover + refColdRentLongTerm)));
	    logger.debug(String.format("TotalNetProfit %8.2f", netEarnings));
	    logger.debug(String.format("Profit %8.2f", payment));
	    logger.debug(String.format("Profit/hour %8.2f", paymentPerHour));
	}
	chart2.getChildren().clear();
	final Label l = new Label("Profit total" + String.format("%6.0f€", payment) + "\n" + "Profit/hour"
		+ String.format("%6.0f€", paymentPerHour));
	l.setWrapText(true);
	chart2.getChildren().add(l);
    }

}

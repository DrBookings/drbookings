package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingFilter;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class OverviewChartController implements Initializable {

    public MainController getMainController() {
	return mainController;
    }

    public void setMainController(final MainController mainController) {
	this.mainController = mainController;
    }

    private final static Logger logger = LoggerFactory.getLogger(OverviewChartController.class);

    private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> updateUI(c));

    private MainController mainController;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	// chart.getData().add(seriesLast);
	chart.setOnMouseClicked(event -> {
	    // if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
	    handleChartClickEvent(event);
	    // }
	});
	updateUI(null);
	CellSelectionManager.getInstance().getSelection().addListener(roomListener);

    }

    private void handleChartClickEvent(final MouseEvent event) {
	chart.setMinHeight(100);
	// System.err.println("Setting min Height");

    }

    public OverviewChartController() {

    }

    @FXML
    private BarChart<String, Number> chart;

    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private Label labelStartDate;
    @FXML
    private Label labelEndDate;

    public void shutDown() {

    }

    private void updateUI(final Change<? extends RoomBean> c) {
	chart.setMinHeight(40);
	if (mainController == null) {
	    return;
	}
	chart.getData().clear();
	yAxis.setLowerBound(0);
	yAxis.setUpperBound(10);
	if (c != null && c.next() && !c.wasAdded()) {
	    return;
	}
	// if (logger.isDebugEnabled()) {
	// logger.debug("Updating UI");
	// }

	final ObservableList<RoomBean> selectedRooms = CellSelectionManager.getInstance().getSelection();
	final List<BookingEntry> selectedBookings = selectedRooms.stream().flatMap(r -> r.getBookingEntries().stream())
		.filter(new BookingFilter(mainController.getGuestNameFilter())).collect(Collectors.toList());

	if (selectedBookings.isEmpty()) {
	    return;
	}

	Collections.sort(selectedBookings);

	final XYChart.Series<String, Number> seriesCurrent = new XYChart.Series<>();
	final NavigableMap<LocalDate, Number> dateToEarnings = new TreeMap<>();
	for (final BookingEntry rb : selectedBookings) {
	    Number n = dateToEarnings.get(rb.getDate());
	    if (n == null) {
		n = 0;
	    }
	    n = n.doubleValue() + rb.getNetEarnings();
	    dateToEarnings.put(rb.getDate(), n);
	}

	yAxis.setUpperBound(
		(int) dateToEarnings.values().stream().mapToDouble(e -> e.doubleValue()).max().getAsDouble());
	yAxis.setTickUnit(100);

	for (final Entry<LocalDate, Number> e : dateToEarnings.entrySet()) {
	    seriesCurrent.getData().add(new XYChart.Data<String, Number>(e.getKey().toString(), e.getValue()));
	}
	chart.getData().add(seriesCurrent);
	if (dateToEarnings.isEmpty()) {
	    labelStartDate.setText(null);
	    labelEndDate.setText(null);
	} else {
	    labelStartDate.setText(dateToEarnings.firstKey().toString().replace("-", "\n"));
	    labelEndDate.setText(dateToEarnings.lastKey().toString().replace("-", "\n"));
	}

    }

}

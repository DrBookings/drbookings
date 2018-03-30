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

import com.github.drbookings.BookingEntryBin;
import com.github.drbookings.CleaningDateFilter;
import com.github.drbookings.PaymentDateFilter;
import com.github.drbookings.model.data.BookingEntries;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.model.BookingEntry;
import com.google.common.collect.Range;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class EarningsViewController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(EarningsViewController.class);
    private static final int DEFAULT_NODE_SIZE = 14;
    @FXML
    XYChart<String, Number> chart;
    @FXML
    Label labelAverage;
    @FXML
    Label labelMax;
    @FXML
    Label labelMin;
    @FXML
    Slider slider;
    @FXML
    Label monthCount;
    private XYChart.Series<String, Number> netProfitTotal = new XYChart.Series<String, Number>();
    //    private XYChart.Series<String, Number> netEarningsBooking = new XYChart.Series<String, Number>();
//    private XYChart.Series<String, Number> netEarningsAirbnb = new XYChart.Series<String, Number>();
    private XYChart.Series<String, Number> netEarningsTotal = new XYChart.Series<String, Number>();
    private XYChart.Series<String, Number> cleaningCosts = new XYChart.Series<String, Number>();
    private XYChart.Series<String, Number> longTermEarnings = new XYChart.Series<String, Number>();
    private MainManager manager;

    static void applyYValueToolTip(XYChart.Data<String, Number> data) {
        final Tooltip t = new Tooltip(String.format("%.2f", data.getYValue()));
        t.setAutoHide(true);

//            t.show(this.chart.getScene().getWindow());

        EventHandler<MouseEvent> onMouseEnteredSeriesListener =
                (MouseEvent event) -> {

                    String msg =
                            "(x: " + event.getX() + ", y: " + event.getY() + ") -- " +
                                    "(sceneX: " + event.getSceneX() + ", sceneY: " + event.getSceneY() + ") -- " +
                                    "(screenX: " + event.getScreenX() + ", screenY: " + event.getScreenY() + ")";

//                        System.err.println(msg);

                    t.setX(event.getScreenX());
                    t.setY(event.getScreenY());
                    t.show(((Node) event.getTarget()).getScene().getWindow());
                };

        EventHandler<MouseEvent> onMouseExitedSeriesListener =
                (MouseEvent event) -> {
                    t.hide();
                };

        data.getNode().setOnMouseEntered(onMouseEnteredSeriesListener);
        data.getNode().setOnMouseExited(onMouseExitedSeriesListener);
    }

    static void applyNodeSize(XYChart.Data<?, ?> data, int prefSize) {
        StackPane stackPane = (StackPane) data.getNode();
        stackPane.setPrefWidth(prefSize);
        stackPane.setPrefHeight(prefSize);
    }

    static void applyNodeSize(XYChart.Data<?, ?> data) {
        applyNodeSize(data, DEFAULT_NODE_SIZE);
    }

    public MainManager getManager() {
        return manager;
    }

    public void setManager(final MainManager manager) {
        this.manager = manager;
        doChart();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        netEarningsTotal.setName("Net Earnings");
        netProfitTotal.setName("Net Profit Total");
//        netEarningsBooking.setName("Net Earnings BookingBean");
//        netEarningsAirbnb.setName("Net Earnings Airbnb");
        longTermEarnings.setName("Net Earnings Long-term (>7d)");
        cleaningCosts.setName("Cleaning Costs");
        chart.getData().addAll(netEarningsTotal, longTermEarnings, netProfitTotal, cleaningCosts);
//        BookingSelectionManager.getInstance().selectionProperty()
//                .addListener((ListChangeListener<BookingEntry>) c -> doChart(c.getList()));
        slider.valueProperty().addListener((c, o, n) -> {
            if (o != null && n != null) {
                int o2 = o.intValue();
                int n2 = n.intValue();
                if (o2 != n2) {
                    doChart();
                }
            } else {
                doChart();
            }
        });
        doChart();
    }

    private void doChart() {
        if (manager != null) {
            int numMonthBack = (int) slider.getValue();
            monthCount.setText(Integer.toString(numMonthBack));
            Range<YearMonth> monthRange = Range.closed(YearMonth.from(LocalDate.now().minusMonths(numMonthBack)), YearMonth.from(LocalDate.now().minusMonths(1)));
            if (logger.isDebugEnabled()) {
                logger.debug("Month range: " + monthRange);
            }
            doChart(manager.getBookingEntries().stream().filter(e -> e.getElement().getDateOfPayment() != null).filter(e -> monthRange.contains(YearMonth.from(e.getElement().getDateOfPayment()))).collect(Collectors.toList()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Manager null");
            }
        }
    }

    private void doChart(Collection<? extends BookingEntry> allElements) {
        if (logger.isDebugEnabled()) {
            logger.debug("Doing chart");
        }

        netProfitTotal.getData().clear();
//        netEarningsBooking.getData().clear();
//        netEarningsAirbnb.getData().clear();
        netEarningsTotal.getData().clear();
        cleaningCosts.getData().clear();
        longTermEarnings.getData().clear();

        final float additionalCosts = SettingsManager.getInstance().getAdditionalCosts();
        final float numberOfRooms = SettingsManager.getInstance().getNumberOfRooms();
        final float totalAdditionalCosts = additionalCosts * numberOfRooms;


        BookingEntryMonthBins bin = new BookingEntryMonthBins(allElements);


        List<Double> numbers = new ArrayList<>();


        for (BookingEntryBin<YearMonth> be : bin.getBins()) {

            final Collection<? extends BookingEntry> bookingsFilteredByPaymentDate = be.getEntries().stream()
                    .filter(new PaymentDateFilter(Range.closed(be.getLabel().atDay(1), be.getLabel().atEndOfMonth()))).collect(Collectors.toList());

            Collection<? extends BookingEntry> longTermBookings = bookingsFilteredByPaymentDate.stream().filter(b -> b.getElement().getNumberOfNights() > 7).collect(Collectors.toList());

            final Collection<? extends BookingEntry> bookingsFilteredByCleaningDate = be.getEntries().stream()
                    .filter(new CleaningDateFilter(Range.closed(be.getLabel().atDay(1), be.getLabel().atEndOfMonth()))).collect(Collectors.toList());

            double totalEarnings = BookingEntries.getNetEarnings(bookingsFilteredByPaymentDate);

            if (totalEarnings < 1) {
                continue;
            }

            double totalEarningsLongTerm = BookingEntries.getNetEarnings(longTermBookings);
//            double earningsBooking = BookingEntries.getNetEarningsBooking(bookingsFilteredByPaymentDate);
//            double earningsAirbnb = BookingEntries.getNetEarningsAirbnb(bookingsFilteredByPaymentDate);
            double cleaningCosts = BookingEntries.getCleaningCosts(bookingsFilteredByCleaningDate);
            double totalNetProfit = totalEarnings - totalAdditionalCosts - cleaningCosts;

            numbers.add(totalNetProfit);

            XYChart.Data<String, Number> data = new XYChart.Data<>(be.getLabel().toString(), totalNetProfit);
            data.setNode(new HoveredThresholdNode(totalNetProfit));
            netProfitTotal.getData().add(data);

//            applyYValueToolTip(data);
//            applyNodeSize(data);

            data = new XYChart.Data<>(be.getLabel().toString(), cleaningCosts);
            data.setNode(new HoveredThresholdNode(cleaningCosts));
            this.cleaningCosts.getData().add(data);

            data = new XYChart.Data<>(be.getLabel().toString(), totalEarnings);
            data.setNode(new HoveredThresholdNode(totalEarnings));
            this.netEarningsTotal.getData().add(data);

            data = new XYChart.Data<>(be.getLabel().toString(), totalEarningsLongTerm);
            data.setNode(new HoveredThresholdNode(totalEarningsLongTerm));
            this.longTermEarnings.getData().add(data);


        }

        labelMax.setText(String.format("%.2f", numbers.stream().mapToDouble(n -> n).max().orElse(0f)));
        labelMin.setText(String.format("%.2f", numbers.stream().mapToDouble(n -> n).min().orElse(0f)));
        labelAverage.setText(String.format("%.2f", numbers.stream().mapToDouble(n -> n).average().orElse(0f)));
    }

    /**
     * a node which displays a value on hover, but is otherwise empty
     */
    class HoveredThresholdNode extends StackPane {
        HoveredThresholdNode(double value) {
            setPrefSize(DEFAULT_NODE_SIZE, DEFAULT_NODE_SIZE);

            final Label label = createDataThresholdLabel(value);
//            getChildren().setAll(label);
            setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getChildren().setAll(label);
                    setCursor(Cursor.NONE);
                    toFront();
                }
            });
            setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    getChildren().clear();
                    setCursor(Cursor.DEFAULT);
                }
            });
        }

        private Label createDataThresholdLabel(double value) {
            final Label label = new Label(String.format("%.2f", value));
            label.getStyleClass().addAll("chart-line-symbol", "chart-series-line");
//            label.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");
            label.setMinSize(Label.USE_PREF_SIZE, Label.USE_PREF_SIZE);
            return label;
        }
    }

}

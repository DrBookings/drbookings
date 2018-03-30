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

import com.github.drbookings.*;
import com.github.drbookings.model.data.BookingEntries;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.CurrencyCellValueFactory;
import com.github.drbookings.ui.IntegerCellValueFactory;
import com.github.drbookings.ui.beans.StatisticsTableBean;
import com.github.drbookings.ui.selection.BookingSelectionManager;
import com.google.common.collect.Range;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class StatsViewController implements Initializable {

	private final static Logger logger = LoggerFactory.getLogger(StatsViewController.class);

	@FXML
	private TableColumn<StatisticsTableBean, Number> cCleaningCosts;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cCleaningFees;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNumberOfCleanings;
	@FXML
    private TableColumn<StatisticsTableBean, Number> cNumberOfPayedNights;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cNumberOfAllNights;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cNumberOfAllBookings;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cNumberOfPayedBookings;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cNightsPercent;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cRelativeFixCosts;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cEarnings;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cNetEarnings;
    @FXML
    private TableColumn<StatisticsTableBean, Number> cEarningsPayout;
    @FXML
	private TableColumn<StatisticsTableBean, Number> cGrossIncome;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNetIncome;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cServiceFees;

//	private final ObjectProperty<Range<LocalDate>> dateRange = new SimpleObjectProperty<>();

	// @FXML
	// private Label totalEarningsPayout;
	// @FXML
	// private Label totalCleaningCosts;

	private final ObservableList<StatisticsTableBean> data = FXCollections.observableArrayList();

	private MainManager mainManager;

	@FXML
	private TableView<StatisticsTableBean> tableView;

	public StatsViewController() {

	}

	public MainManager getMainManager() {
		return mainManager;
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
        cCleaningCosts.setCellFactory(new CurrencyCellValueFactory<>());
        cCleaningFees.setCellFactory(new CurrencyCellValueFactory<>());
        cNumberOfAllBookings.setCellFactory(new IntegerCellValueFactory<>());
        cNumberOfPayedBookings.setCellFactory(new IntegerCellValueFactory<>());
        cNumberOfPayedNights.setCellFactory(new IntegerCellValueFactory<>());
        cNumberOfAllNights.setCellFactory(new IntegerCellValueFactory<>());
        cNumberOfCleanings.setCellFactory(new IntegerCellValueFactory<>());
        cNightsPercent.setCellFactory(new CurrencyCellValueFactory<>());
        cRelativeFixCosts.setCellFactory(new CurrencyCellValueFactory<>());
        cEarnings.setCellFactory(new CurrencyCellValueFactory<>());
        cNetEarnings.setCellFactory(new CurrencyCellValueFactory<>());
        cEarningsPayout.setCellFactory(new CurrencyCellValueFactory<>());
        cGrossIncome.setCellFactory(new CurrencyCellValueFactory<>());
        cNetIncome.setCellFactory(new CurrencyCellValueFactory<>());
        cServiceFees.setCellFactory(new CurrencyCellValueFactory<>());

		FXUtils.makeHeaderWrappable(cCleaningCosts);
		FXUtils.makeHeaderWrappable(cCleaningFees);
		FXUtils.makeHeaderWrappable(cNumberOfPayedBookings);
        FXUtils.makeHeaderWrappable(cNumberOfAllBookings);
        FXUtils.makeHeaderWrappable(cNumberOfPayedNights);
        FXUtils.makeHeaderWrappable(cNumberOfAllNights);
        FXUtils.makeHeaderWrappable(cNumberOfCleanings);
        FXUtils.makeHeaderWrappable(cNightsPercent);
        FXUtils.makeHeaderWrappable(cRelativeFixCosts);
        FXUtils.makeHeaderWrappable(cEarnings);
        FXUtils.makeHeaderWrappable(cNetEarnings);
        FXUtils.makeHeaderWrappable(cEarningsPayout);
        FXUtils.makeHeaderWrappable(cGrossIncome);
        FXUtils.makeHeaderWrappable(cNetIncome);
        FXUtils.makeHeaderWrappable(cServiceFees);

        tableView.setOnContextMenuRequested(e -> System.out.println("Context menu requested " + e));
        tableView.setTableMenuButtonVisible(true);
        tableView.setItems(data);

        BookingSelectionManager.getInstance().selectionProperty().addListener((ListChangeListener<BookingEntry>) c -> {
            while (c.next()) {

            }
            updateUI(c.getList());
        });

		updateUI(BookingSelectionManager.getInstance().selectionProperty());

		// setHideCleaningStatistics(SettingsManager.getInstance().isHideCleaningStatistics());
		// SettingsManager.getInstance().hideCleaningStatisticsProperty()
		// .addListener((ChangeListener<Boolean>) (v, o, n) ->
		// setHideCleaningStatistics(n));
	}

	// private void setHideCleaningStatistics(final boolean hide) {
	// if (hide) {
	// hideCleaningStatistics();
	// } else {
	// showCleaningStatistics();
	// }
	// }

	// private void hideCleaningStatistics() {
	// tableView.getColumns().remove(cCleaningCosts);
	// tableView.getColumns().remove(cCleaningFees);
	// tableView.getColumns().remove(cNumberOfCleanings);
	// }

	// private void showCleaningStatistics() {
	// if (!tableView.getColumns().contains(cCleaningCosts)) {
	// tableView.getColumns().add(cCleaningCosts);
	// }
	// if (!tableView.getColumns().contains(cCleaningFees)) {
	// tableView.getColumns().add(cCleaningFees);
	// }
	// if (!tableView.getColumns().contains(cNumberOfCleanings)) {
	// tableView.getColumns().add(cNumberOfCleanings);
	// }
	// }

	public void setMainManager(final MainManager mainManager) {
		this.mainManager = mainManager;

	}

	public void shutDown() {

	}

    private void updateUI(final BookingsByOrigin<BookingEntry> bookings, final Range<LocalDate> dateRange) {
        if (logger.isDebugEnabled()) {
            logger.debug("Statistics for\n" + BookingEntries.toBookings(bookings.getAllBookings()).stream().map(i ->
                    i.toString())
                    .collect(Collectors.joining("\n")));
        }
		final float allAllNigths = BookingEntries.countNights(bookings, false);
		final NavigableSet<LocalDate> allDates = bookings.getAllBookings(true).stream().map(b -> b.getDate())
				.collect(Collectors.toCollection(TreeSet::new));
		long monthCount = TemporalQueries.countOccurrences(allDates,
				SettingsManager.getInstance().getFixCostsPaymentDay());
		if (logger.isDebugEnabled()) {
			logger.debug("Month count: " + monthCount);
		}
		if (monthCount < 1) {
			monthCount = 1;
			if (logger.isDebugEnabled()) {
				logger.debug("Month count (corrected): " + monthCount);
			}
		}
		final float additionalCosts = SettingsManager.getInstance().getAdditionalCosts() * monthCount;
		final float numberOfRooms = SettingsManager.getInstance().getNumberOfRooms();
		final float totalAdditionalCosts = additionalCosts * numberOfRooms;
		if (logger.isDebugEnabled()) {
			logger.debug("Fix costs total: " + totalAdditionalCosts);
		}
		for (final Entry<BookingOrigin, Collection<BookingEntry>> e : bookings.getMap().entrySet()) {

            final Collection<? extends BookingEntry> bookingsFilteredByPaymentDate = e.getValue().stream()
                    .filter(new PaymentDateFilter(dateRange)).collect(Collectors.toList());

            final Collection<? extends BookingEntry> bookingsFilteredByCleaningDate = e.getValue().stream()
                    .filter(new CleaningDateFilter(dateRange)).collect(Collectors.toList());

            final int numberOfAllBookings = (int) BookingEntries.countBookings(new BookingsByOrigin<>
                    (e.getValue()), false);


            final int numberOfPayedBookings = (int) BookingEntries.countBookings(new BookingsByOrigin<>
                    (e.getValue()), false);

            final int numberOfAllNights = (int) BookingEntries.countNights(new BookingsByOrigin<>
                            (e.getValue())
                    , false);

            final int numberOfPayedNights = (int) BookingEntries.countNights(new BookingsByOrigin<>
                            (bookingsFilteredByPaymentDate)
                    , false);

            final float percentage;

            if (StringUtils.isBlank(e.getKey().getName())) {
                percentage = 0;
            } else {
                percentage = numberOfAllNights / allAllNigths * 100f;
            }
            if (logger.isDebugEnabled()) {
                logger.debug(e.getKey() + " percentage of all nights: " + percentage);
            }
            final double relativeFixCosts = totalAdditionalCosts * percentage / 100;
            if (logger.isDebugEnabled()) {
                logger.debug(e.getKey() + " relative fix costs " + relativeFixCosts);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(e.getKey() + " number of bookings (all/payed): " + numberOfAllBookings + "/" +
                        numberOfPayedBookings);
            }
            if (logger.isDebugEnabled()) {
                logger.debug(e.getKey() + ": Number of nights (all/payed): " + numberOfAllNights + "/" +
                        numberOfPayedNights);
            }



            if (logger.isDebugEnabled()) {
                Set<Guest> set = e.getValue().stream().map(b -> b.getElement().getGuest()).collect(Collectors.toCollection
                        (LinkedHashSet::new));
                List<Guest> list = e.getValue().stream().filter(b -> !b.isCheckOut()).map(b -> b.getElement().getGuest
                        ()).collect
                        (Collectors
                                .toCollection
                                        (ArrayList::new));
                 StringBuilder sb = new StringBuilder(e.getKey() + " guest and nights (all):");
                int cnt = 1;
                int cnt2 = 0;
                for (final Guest guest : set) {
                    final int cnt3 = Collections.frequency(list, guest);

                    sb.append(String.format("%n%4d%20s%4d", cnt++, guest.getName(), cnt3));
                    cnt2 += cnt3;
                }
                sb.append(String.format("%n%24s%4d", "Total", cnt2));

                logger.debug(sb.toString());

                set = bookingsFilteredByPaymentDate.stream().map(b -> b.getElement().getGuest()).collect(Collectors.toCollection
                        (LinkedHashSet::new));
                list = bookingsFilteredByPaymentDate.stream().filter(b -> !b.isCheckOut()).map(b -> b.getElement().getGuest
                        ()).collect
                        (Collectors
                                .toCollection
                                        (ArrayList::new));
                sb = new StringBuilder(e.getKey() + " guest and nights (payed):");
                cnt = 1;
                cnt2 = 0;
                for (final Guest guest : set) {
                    final int cnt3 = Collections.frequency(list, guest);

                    sb.append(String.format("%n%4d%20s%4d", cnt++, guest.getName(), cnt3));
                    cnt2 += cnt3;
                }
                sb.append(String.format("%n%24s%4d", "Total", cnt2));

                logger.debug(sb.toString());

            }


            final StatisticsTableBean b = StatisticsTableBean.build(e.getKey().getName(),
                    bookingsFilteredByPaymentDate);
            StatisticsTableBean.applyCleaningStuff(b, bookingsFilteredByCleaningDate);
            b.setFixCosts((float) relativeFixCosts);
            b.setNightsPercent(percentage);
            b.setNumberOfPayedNights(numberOfPayedNights);
            b.setNumberOfAllNights(numberOfAllNights);
            b.setNumberOfPayedBookings(numberOfPayedBookings);
            b.setNumberOfAllBookings(numberOfAllBookings);
            data.add(b);
        }
        // add a total row

		final float relativeFixCosts = totalAdditionalCosts;
		final StatisticsTableBean b = StatisticsTableBean.buildSum(data);
		b.setFixCosts(relativeFixCosts);
		b.setNightsPercent(100);
        data.add(b);
    }

	private void updateUI(final Collection<? extends BookingEntry> bookings) {
        data.clear();
        if (bookings == null || bookings.isEmpty()) {
            return;
        }


		final BookingsByOrigin<BookingEntry> bo = new BookingsByOrigin<>(bookings);
        updateUI(bo, LocalDates.getDateRange(bookings.stream().map(e -> e.getDate()).collect(Collectors.toList())));
        // updateTotals();
    }

	// private void updateTotals() {
	// final double sumTotalEarningsPayout = data.stream().mapToDouble(b ->
	// b.getEarningsPayout()).sum();
	// final double sumTotalCleaningCosts = data.stream().mapToDouble(b ->
	// b.getCleaningCosts()).sum();
	// totalEarningsPayout.setText(NumberFormat.getIntegerInstance().format(sumTotalEarningsPayout));
	// totalCleaningCosts.setText(NumberFormat.getIntegerInstance().format(sumTotalCleaningCosts));
	//
	// }

}

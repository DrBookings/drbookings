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
import java.util.Collection;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.ResourceBundle;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.FXUtils;
import com.github.drbookings.LocalDates;
import com.github.drbookings.PaymentDateFilter;
import com.github.drbookings.TemporalQueries;
import com.github.drbookings.model.data.BookingEntries;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.CurrencyCellValueFactory;
import com.github.drbookings.ui.IntegerCellValueFactory;
import com.github.drbookings.ui.beans.StatisticsTableBean;
import com.github.drbookings.ui.selection.BookingSelectionManager;
import com.google.common.collect.Range;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class StatsViewController implements Initializable {

	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(StatsViewController.class);

	@FXML
	private TableColumn<StatisticsTableBean, Number> cCleaningCosts;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cCleaningFees;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNumberOfCleanings;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNumberOfNights;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNumberOfBookings;
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

	private final ObjectProperty<Range<LocalDate>> dateRange = new SimpleObjectProperty<>();

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
		cNumberOfBookings.setCellFactory(new IntegerCellValueFactory<>());
		cNumberOfNights.setCellFactory(new IntegerCellValueFactory<>());
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
		FXUtils.makeHeaderWrappable(cNumberOfBookings);
		FXUtils.makeHeaderWrappable(cNumberOfNights);
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

		BookingSelectionManager.getInstance().selectionProperty().addListener(new ListChangeListener<BookingEntry>() {

			@Override
			public void onChanged(final javafx.collections.ListChangeListener.Change<? extends BookingEntry> c) {
				while (c.next()) {

				}

				updateUI(c.getList());
			}
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

	private void updateUI(final BookingsByOrigin<BookingEntry> bookings) {
		final float allNights = BookingEntries.countNights(bookings, false);
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
			final float thisNights = BookingEntries.countNights(new BookingsByOrigin<>(e.getValue()), false);
			final float percentage;
			if (StringUtils.isBlank(e.getKey().getName())) {
				percentage = 0;
			} else {
				percentage = thisNights / allNights * 100;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Percentage: " + percentage);
			}
			final double relativeFixCosts = totalAdditionalCosts * percentage / 100;
			if (logger.isDebugEnabled()) {
				logger.debug(e.getKey() + " relative fix costs " + relativeFixCosts);
			}
			final StatisticsTableBean b = StatisticsTableBean.build(e.getKey().getName(), e.getValue());
			b.setFixCosts((float) relativeFixCosts);
			b.setNightsPercent(percentage);
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
		dateRange.set(LocalDates.getDateRange(bookings.stream().map(e -> e.getDate()).collect(Collectors.toList())));
		final Collection<? extends BookingEntry> bookingsFiltered = bookings.stream()
				.filter(new PaymentDateFilter(dateRange.get())).collect(Collectors.toList());
		final BookingsByOrigin<BookingEntry> bo = new BookingsByOrigin<>(bookingsFiltered);
		updateUI(bo);
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

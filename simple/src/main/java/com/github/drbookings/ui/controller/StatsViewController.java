package com.github.drbookings.ui.controller;

import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.FXUtils;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.BookingOrigin;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.IntegerCellValueFactory;
import com.github.drbookings.ui.selection.BookingSelectionManager;

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
	private TableColumn<StatisticsTableBean, Number> cPayoutUnknown;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cTotalPayout;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNightsPercent;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cRelativeFixCosts;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cEarnings;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cEarningsPayout;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cGrossEarnings;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cNetEarnings;
	@FXML
	private TableColumn<StatisticsTableBean, Number> cPerformance;
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
		cCleaningCosts.setCellFactory(new IntegerCellValueFactory<>());
		cCleaningFees.setCellFactory(new IntegerCellValueFactory<>());
		cNumberOfBookings.setCellFactory(new IntegerCellValueFactory<>());
		cNumberOfNights.setCellFactory(new IntegerCellValueFactory<>());
		cPayoutUnknown.setCellFactory(new IntegerCellValueFactory<>());
		cTotalPayout.setCellFactory(new IntegerCellValueFactory<>());
		cNumberOfCleanings.setCellFactory(new IntegerCellValueFactory<>());
		cNightsPercent.setCellFactory(new IntegerCellValueFactory<>());
		cRelativeFixCosts.setCellFactory(new IntegerCellValueFactory<>());
		cEarnings.setCellFactory(new IntegerCellValueFactory<>());
		cEarningsPayout.setCellFactory(new IntegerCellValueFactory<>());
		cGrossEarnings.setCellFactory(new IntegerCellValueFactory<>());
		cNetEarnings.setCellFactory(new IntegerCellValueFactory<>());
		cPerformance.setCellFactory(new IntegerCellValueFactory<>());
		FXUtils.makeHeaderWrappable(cCleaningCosts);
		FXUtils.makeHeaderWrappable(cCleaningFees);
		FXUtils.makeHeaderWrappable(cNumberOfBookings);
		FXUtils.makeHeaderWrappable(cNumberOfNights);
		FXUtils.makeHeaderWrappable(cPayoutUnknown);
		FXUtils.makeHeaderWrappable(cTotalPayout);
		FXUtils.makeHeaderWrappable(cNumberOfCleanings);
		FXUtils.makeHeaderWrappable(cNightsPercent);
		FXUtils.makeHeaderWrappable(cRelativeFixCosts);
		FXUtils.makeHeaderWrappable(cEarnings);
		FXUtils.makeHeaderWrappable(cEarningsPayout);
		FXUtils.makeHeaderWrappable(cGrossEarnings);
		FXUtils.makeHeaderWrappable(cNetEarnings);
		FXUtils.makeHeaderWrappable(cPerformance);

		tableView.setOnContextMenuRequested(e -> System.out.println("Context menu requested " + e));
		tableView.setTableMenuButtonVisible(true);
		tableView.setItems(data);
		updateUI(BookingSelectionManager.getInstance().bookingsProperty());
		BookingSelectionManager.getInstance().bookingsProperty().addListener(new ListChangeListener<Booking>() {

			@Override
			public void onChanged(final javafx.collections.ListChangeListener.Change<? extends Booking> c) {
				while (c.next()) {

				}
				updateUI(c.getList());
			}
		});

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

	private void updateUI(final Collection<? extends Booking> bookings) {
		data.clear();
		if (bookings != null && !bookings.isEmpty()) {
			final BookingsByOrigin<Booking> bo = new BookingsByOrigin<>(bookings);
			final float allNights = bo.getAllBookings().stream().mapToLong(b -> b.getNumberOfNights()).sum();
			final float additionalCosts = SettingsManager.getInstance().getAdditionalCosts();
			final float numberOfRooms = SettingsManager.getInstance().getNumberOfRooms();
			final float totalAdditionalCosts = additionalCosts * numberOfRooms;
			for (final Entry<BookingOrigin, Collection<Booking>> e : bo.getMap().entrySet()) {
				final float thisNights = e.getValue().stream().mapToLong(b -> b.getNumberOfNights()).sum();
				final float percentage = thisNights / allNights * 100;
				final float relativeFixCosts = totalAdditionalCosts * percentage / 100;
				final StatisticsTableBean b = StatisticsTableBean.build(e.getKey().getName(), e.getValue());
				b.setFixCosts(relativeFixCosts);
				b.setNightsPercent(percentage);
				data.add(b);
			}
			// add a total row
			final float thisNights = bookings.stream().mapToLong(b -> b.getNumberOfNights()).sum();
			final float percentage = thisNights / allNights * 100;
			final float relativeFixCosts = totalAdditionalCosts * percentage / 100;
			final StatisticsTableBean b = StatisticsTableBean.build("all", bookings);
			b.setFixCosts(relativeFixCosts);
			b.setNightsPercent(percentage);
			data.add(b);

		}
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

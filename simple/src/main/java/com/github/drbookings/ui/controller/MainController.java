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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.BookingBean;
import com.github.drbookings.BookingEntries;
import com.github.drbookings.BookingEntry;
import com.github.drbookings.BookingsByOrigin;
import com.github.drbookings.DataStoreCore;
import com.github.drbookings.DateBean;
import com.github.drbookings.DrBookingsApplication;
import com.github.drbookings.LocalDates;
import com.github.drbookings.Room;
import com.github.drbookings.RoomBean;
import com.github.drbookings.SettingsManager;
import com.github.drbookings.SimpleUIData;
import com.github.drbookings.UIData;
import com.github.drbookings.google.GoogleCalendarSync;
import com.github.drbookings.ical.AirbnbICalParser;
import com.github.drbookings.ical.ICalBookingFactory;
import com.github.drbookings.ical.XlsxBookingFactory;
import com.github.drbookings.io.FromXMLReader;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ser.DataStoreCoreSer;
import com.github.drbookings.ser.DataStoreFactory;
import com.github.drbookings.ser.UnmarshallListener;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.AbstractDrBookingsService;
import com.github.drbookings.ui.BookingReaderService;
import com.github.drbookings.ui.EarningsViewFactory;
import com.github.drbookings.ui.FXUIUtils;
import com.github.drbookings.ui.OccupancyCellFactory;
import com.github.drbookings.ui.OccupancyCellValueFactory;
import com.github.drbookings.ui.StatusLabelStringFactory;
import com.github.drbookings.ui.StudioCellFactory;
import com.github.drbookings.ui.concurrent.BookingExportService;
import com.github.drbookings.ui.dialogs.BookingDetailsDialogFactory;
import com.github.drbookings.ui.dialogs.CleaningPlanDialogFactory;
import com.github.drbookings.ui.dialogs.EarningsChartFactory;
import com.github.drbookings.ui.dialogs.GeneralSettingsDialogFactory;
import com.github.drbookings.ui.dialogs.ModifyBookingDialogFactory;
import com.github.drbookings.ui.dialogs.NightlyRateChartFactory;
import com.github.drbookings.ui.dialogs.ProfitChartFactory;
import com.github.drbookings.ui.dialogs.RoomDetailsDialogFactory;
import com.github.drbookings.ui.dialogs.StatisticsFactory;
import com.github.drbookings.ui.selection.BookingSelectionManager;
import com.github.drbookings.ui.selection.DateBeanSelectionManager;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;
import com.google.common.collect.Range;
import com.jcabi.manifests.Manifests;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * Controller for the main window.
 *
 * @author Alexander Kerner
 *
 */
public class MainController implements Initializable {

    private class ClearGoogleCalendarService extends DrBookingService<Void> {

	public ClearGoogleCalendarService() {

	}

	@Override
	protected Task<Void> createTask() {

	    return new Task<Void>() {

		@Override
		protected Void call() throws Exception {
		    new GoogleCalendarSync(getManager()).init().clearAll();
		    return null;
		}
	    };
	}
    }

    @Deprecated
    private abstract class DrBookingService<T> extends AbstractDrBookingsService<T> {
	public DrBookingService() {
	    super(progressLabel);

	    addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, event -> {
		setWorking(false, false);
	    });

	    addEventHandler(WorkerStateEvent.WORKER_STATE_SCHEDULED, event -> {
		setWorking(true);
	    });

	    addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, event -> {
		setWorking(false);

	    });
	}
    }

    private DataStoreCore data;

    private final UIData uiData;

    private class OpenFileService extends DrBookingService<DataStoreCoreSer> {

	private final File file;

	private final UnmarshallListener l;

	public OpenFileService(final File file) {
	    super();
	    this.file = file;
	    l = new UnmarshallListener();

	    setOnScheduled(e -> {
		setWorking(true);
		progressLabel.textProperty()
			.bind(Bindings.createObjectBinding(buildProgressString(l), l.bookingCountProperty()));
	    });

	    setOnSucceeded(e -> {
		progressLabel.setText("Rendering..");
		try {

		    data = DataStoreFactory.build(getValue());
		    uiData.loadFrom(data);

		    scrollToToday();
		} catch (final Exception e1) {
		    logger.error(e1.getLocalizedMessage(), e1);
		    progressLabel.setText("Error: " + e1);
		}
		progressLabel.setText(null);
		setWorking(false);
		if (logger.isInfoEnabled()) {
		    logger.info("Read data from " + file.getAbsolutePath());
		}
	    });
	    setOnFailed(event -> {
		progressLabel.setText(event.getSource().getException().getLocalizedMessage());
		setWorking(false);
	    });

	}

	@Override
	protected Task<DataStoreCoreSer> createTask() {
	    return new Task<DataStoreCoreSer>() {

		@Override
		protected DataStoreCoreSer call() throws Exception {
		    SettingsManager.getInstance().setDataFile(file);
		    final DataStoreCoreSer result = new FromXMLReader().setListener(l).readFromFile(file);
		    return result;
		}

	    };
	}
    }

    class SaveService extends DrBookingService<Void> {

	private final File file;

	SaveService(final File file) {
	    this.file = file;
	    setOnSucceeded(e -> {
		if (logger.isInfoEnabled()) {
		    logger.info("Saved");
		}
	    });
	}

	@Override
	protected Task<Void> createTask() {
	    return new SaveTask(file);
	}
    }

    class SaveTask extends Task<Void> {

	private final File file;

	SaveTask(final File file) {
	    this.file = file;
	}

	@Override
	protected Void call() throws Exception {
	    new XMLStorage().save(getManager(), file);
	    return null;
	}
    }

    private class WriteToGoogleCalendarService extends DrBookingService<Void> {

	public WriteToGoogleCalendarService() {

	}

	@Override
	protected Task<Void> createTask() {

	    return new Task<Void>() {

		@Override
		protected Void call() throws Exception {

		    new GoogleCalendarSync(getManager()).init().clear().write();
		    return null;
		}
	    };
	}
    }

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.00");
    private static final short DEFAULT_THREAD_WAIT_SECONDS = 30;
    public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final double cleaningDialogWidth = 300;

    private static final double cleaningDialogHeight = 400;

    private BookingDetailsDialogFactory bookingDetailsDialogFactory;
    @FXML
    private Button buttonAddBooking;
    @FXML
    private Button buttonGoHome;
    // @FXML
    // private Button buttonSelectCurrentMonth;
    // @FXML
    // private Button buttonSelectLastMonth;
    // @FXML
    // private Button buttonSelectLastThreeMonth;
    // @FXML
    // private Button buttonSelectNextMonth;
    // @FXML
    // private Button buttonSelectPrevMonth;
    @FXML
    private Button clearFilterButton;
    private EarningsChartFactory earningsChartFactory;
    private EarningsViewFactory earningsViewFactory;
    @FXML
    private Label filterBookingsLabel;
    private GeneralSettingsDialogFactory generalSettingsDialogFactory;
    @FXML
    private TextField guestNameFilterInput;
    private final MainManager manager;
    @FXML
    private MenuItem menuItemExit;
    @FXML
    private MenuItem menuItemOpen;
    private ModifyBookingDialogFactory modifyBookingDialogFactory;
    private StatisticsFactory monthlyMoneyFactory;
    private NightlyRateChartFactory nightlyRateChartFactory;
    @FXML
    private Node node;
    private ProfitChartFactory profitChartFactory;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    private RoomDetailsDialogFactory roomDetailsDialogFactory;

    private final ObservableSet<Integer> rowsWithSelectedCells = FXCollections.observableSet();

    @FXML
    private Label selectedDatesLabel;

    @FXML
    private Label statusLabel;

    @FXML
    private TableView<DateBean> tableView;

    public MainController() {
	manager = MainManager.getInstance();
	uiData = new SimpleUIData();
    }

    private void addBooking() {
	final Optional<RoomBean> room = RoomBeanSelectionManager.getInstance().getFirstSelected();

	if (logger.isDebugEnabled()) {
	    logger.debug("Adding booking  " + (room.isPresent() ? "for " + room.get() : ""));
	}
	Platform.runLater(() -> showAddBookingDialog((room.isPresent() ? room.get().getDate() : null),
		(room.isPresent() ? room.get().getName() : null)));

    }

    private void addCleaning() {
	final Optional<RoomBean> room = RoomBeanSelectionManager.getInstance().getFirstSelected();

	if (logger.isDebugEnabled()) {
	    logger.debug("Adding cleaning  " + (room.isPresent() ? "for " + room.get() : ""));
	}
	Platform.runLater(() -> showAddCleaningDialog((room.isPresent() ? room.get().getDate() : null),
		(room.isPresent() ? room.get().getName() : null)));

    }

    private void addDateColumn() {
	final TableColumn<DateBean, LocalDate> col = new TableColumn<>("Date");
	col.setCellValueFactory(new PropertyValueFactory<>("date"));
	col.setCellFactory(column -> {
	    return new TableCell<DateBean, LocalDate>() {
		@Override
		protected void updateItem(final LocalDate item, final boolean empty) {
		    super.updateItem(item, empty);
		    if ((item == null) || empty) {
			setText(null);
			setStyle("");
		    } else {
			setText(DrBookingsApplication.DATE_FORMATTER.format(item));
		    }
		}
	    };
	});
	col.getStyleClass().addAll("center-left");
	tableView.getColumns().add(col);

    }

    private void addEarningsColumn() {
	final TableColumn<DateBean, Number> col = new TableColumn<>("TotalEarnings");
	col.setCellValueFactory(new PropertyValueFactory<>("totalEarnings"));
	col.setCellFactory(column -> {
	    return new TableCell<DateBean, Number>() {

		@Override
		protected void updateItem(final Number item, final boolean empty) {
		    super.updateItem(item, empty);
		    if ((item == null) || empty) {
			setText(null);
		    } else {
			setText(decimalFormat.format(item));
		    }
		}
	    };

	});
	col.getStyleClass().add("opace");
	tableView.getColumns().add(col);

    }

    private void addOccupancyRateColumn() {
	final TableColumn<DateBean, Number> col = new TableColumn<>("OccupancyRate");
	col.setCellFactory(new OccupancyCellFactory());
	col.setCellValueFactory(new OccupancyCellValueFactory());
	tableView.getColumns().add(col);
    }

    private Callable<String> buildProgressString(final UnmarshallListener listener) {
	return () -> "Bookings read: " + listener.getBookingCount();
    }

    void clearData() {
	getManager().clearData();
    }

    private void deleteSelected() {
	final ObservableList<TablePosition> selectedItems = tableView.getSelectionModel().getSelectedCells();
	if (logger.isDebugEnabled()) {
	    logger.debug("Delete " + selectedItems);
	}
	for (final TablePosition<DateBean, ?> tp : selectedItems) {
	    final int c = tp.getColumn();
	    final int r = tp.getRow();
	    final TableColumn<DateBean, ?> tableColumn = tp.getTableColumn();
	    final Object cellData = tableColumn.getCellData(r);
	    if (logger.isDebugEnabled()) {
		logger.debug("Delete in column " + c + ", " + cellData);
	    }
	    if (cellData instanceof DateBean) {
		final DateBean db = (DateBean) cellData;
		if (db.getRooms().isEmpty()) {

		} else {
		    final RoomBean rb = db.getRoom(new Room("" + c));
		    if (rb.isEmpty()) {
			continue;
		    }
		    final BookingBean booking = rb.getFilteredBookingEntry().toList().get(0).getElement();
		    if (logger.isDebugEnabled()) {
			logger.debug("Deleting " + booking);
		    }
		    manager.removeBooking(booking);
		}
	    }
	}
    }

    @FXML
    public void exportBookings() {
	final List<BookingEntry> bookingsSelected = BookingSelectionManager.getInstance().getSelection();
	new BookingExportService(BookingEntries.toBookings(bookingsSelected)).start();
    }

    private ListChangeListener<TablePosition> getCellSelectionListener() {
	return change -> {
	    final List<RoomBean> cells = new ArrayList<>();
	    final List<DateBean> dates = new ArrayList<>();
	    for (final TablePosition tp : change.getList()) {
		final int r = tp.getRow();
		final int c = tp.getColumn();
		final Object cell = tp.getTableColumn().getCellData(r);
		if (cell instanceof DateBean) {
		    final RoomBean room = ((DateBean) cell).getRoom(new Room("" + c));
		    if (room != null) {
			cells.add(room);
		    }
		    dates.add((DateBean) cell);
		}
	    }
	    RoomBeanSelectionManager.getInstance().setSelection(cells);
	    DateBeanSelectionManager.getInstance().setSelection(dates);
	};
    }

    private int[] getCurrentMonthIndicies() {
	return getIndicies(date -> LocalDates.isCurrentMonth(date));

    }

    public String getGuestNameFilter() {
	return guestNameFilterInput.getText();
    }

    private int[] getIndicies(final Predicate<LocalDate> p) {
	final List<Integer> result = new ArrayList<>();
	for (int index = 0; index < tableView.getItems().size(); index++) {
	    final LocalDate date = tableView.getItems().get(index).getDate();
	    if (p.test(date)) {
		result.add(index);
	    }
	}
	if (result.isEmpty())
	    return new int[0];
	return ArrayUtils.toPrimitive(result.toArray(new Integer[] { result.size() }));

    }

    private int[] getLastMonthIndicies() {
	return getIndicies(date -> LocalDates.isLastMonth(date));

    }

    private int[] getLastThreeMonthIndicies() {
	return getIndicies(date -> LocalDates.isLastThreeMonths(date));

    }

    public MainManager getManager() {
	return manager;
    }

    private int[] getNextMonthIndicies(final YearMonth selectedMonth) {
	return getIndicies(date -> LocalDates.isNextMonth(selectedMonth, date));

    }

    private int[] getPrevMonthIndicies(final YearMonth selectedMonth) {
	return getIndicies(date -> LocalDates.isPrevMonth(selectedMonth, date));

    }

    private Callback<TableView<DateBean>, TableRow<DateBean>> getRowFactory() {
	return param -> {

	    final TableRow<DateBean> row = new TableRow<DateBean>() {
		@Override
		protected void updateItem(final DateBean item, final boolean empty) {
		    super.updateItem(item, empty);
		    getStyleClass().removeAll("now", "end-of-month");
		    if (empty || (item == null)) {

		    } else if (item.getDate().isEqual(LocalDate.now())) {
			getStyleClass().add("now");
		    } else if (item.getDate()
			    .equals(item.getDate().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()))) {
			getStyleClass().add("end-of-month");
		    } else {

		    }
		}
	    };

	    // final PseudoClass rowContainsSelectedCell =
	    // PseudoClass.getPseudoClass("contains-selection");
	    // final BooleanBinding containsSelection =
	    // Bindings.createBooleanBinding(
	    // () -> rowsWithSelectedCells.contains(row.getIndex()),
	    // rowsWithSelectedCells, row.indexProperty());
	    // containsSelection.addListener((obs, didContainSelection,
	    // nowContainsSelection) -> row
	    // .pseudoClassStateChanged(rowContainsSelectedCell,
	    // nowContainsSelection));
	    return row;
	};
    }

    private LocalDate getSelectedDate() {
	final ObservableList<DateBean> selectedDate = tableView.getSelectionModel().getSelectedItems();
	if (logger.isDebugEnabled()) {
	    logger.debug("Selected date: " + selectedDate);
	}
	LocalDate selectedDate2;
	if ((selectedDate == null) || selectedDate.isEmpty()) {
	    selectedDate2 = LocalDate.now();
	} else {
	    selectedDate2 = selectedDate.get(0).getDate();
	}
	return selectedDate2;
    }

    @FXML
    private void handleButtonAddBooking(final ActionEvent event) {
	Platform.runLater(() -> showAddBookingDialog());
    }

    @FXML
    private void handleButtonClearFilter(final ActionEvent event) {
	Platform.runLater(() -> guestNameFilterInput.clear());
    }

    @FXML
    private void handleButtonGoHome(final ActionEvent event) {
	Platform.runLater(() -> scrollToToday());
    }

    @FXML
    private void handleButtonSelectCurrentMonth(final ActionEvent event) {
	Platform.runLater(() -> selectCurrentMonthFX());
    }

    @FXML
    private void handleButtonSelectLastMonth(final ActionEvent event) {
	Platform.runLater(() -> selectLastMonthFX());
    }

    @FXML
    private void handleButtonSelectLastThreeMonth(final ActionEvent event) {
	Platform.runLater(() -> selectLastThreeMonthFX());
    }

    @FXML
    private void handleButtonSelectNextMonth(final ActionEvent event) {
	Platform.runLater(() -> selectNextMonthFX());
    }

    @FXML
    private void handleButtonSelectPrevMonth(final ActionEvent event) {
	Platform.runLater(() -> selectPrevMonthFX());
    }

    @FXML
    private void handleMenuItemAbout(final ActionEvent event) {
	Platform.runLater(() -> showAbout());
    }

    @FXML
    private void handleMenuItemBookingDetails(final ActionEvent event) {
	Platform.runLater(this::showBookingDetails);
    }

    @FXML
    private void handleMenuItemCleaningPlan(final ActionEvent event) {
	Platform.runLater(this::showCleaningPlan);
    }

    @FXML
    private void handleMenuItemClearData(final ActionEvent event) {
	Platform.runLater(this::clearData);
    }

    @FXML
    private void handleMenuItemClearGoogleCalendar(final ActionEvent event) {
	final ClearGoogleCalendarService s = new ClearGoogleCalendarService();
	s.start();
    }

    @FXML
    private void handleMenuItemEarnings(final ActionEvent event) {
	Platform.runLater(this::showEarningsView);
    }

    @FXML
    private void handleMenuItemOpen(final ActionEvent event) {

	Platform.runLater(() -> openFile());

    }

    @FXML
    private void handleMenuItemOpenAirbnbICal(final ActionEvent event) {
	if (logger.isDebugEnabled()) {
	    logger.debug("Opening Airbnb iCal");
	}

	final FileChooser fileChooser = new FileChooser();
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("iCal", Arrays.asList("*.ics", "*.ICS")),
		new FileChooser.ExtensionFilter("All Files", "*"));
	fileChooser.setTitle("Open Airbnb iCal");
	final File file = fileChooser.showOpenDialog(node.getScene().getWindow());
	if (file != null) {
	    try {
		final BookingReaderService reader = new BookingReaderService(getManager(), new ICalBookingFactory(file,
			new AirbnbICalParser(SettingsManager.getInstance().getRoomNameMappings())));
		reader.start();
	    } catch (ClassNotFoundException | IOException e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	}
    }

    @FXML
    private void handleMenuItemOpenBookingExcel(final ActionEvent event) {
	if (logger.isDebugEnabled()) {
	    logger.debug("Opening BookingBean Excel");
	}
	final FileChooser fileChooser = new FileChooser();
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("Excel", Arrays.asList("*.xls", "*.XLS")),
		new FileChooser.ExtensionFilter("All Files", "*"));
	fileChooser.setTitle("Open BookingBean Excel");
	final File file = fileChooser.showOpenDialog(node.getScene().getWindow());
	if (file != null) {
	    final BookingReaderService reader = new BookingReaderService(getManager(), new XlsxBookingFactory(file));
	    reader.start();
	}
    }

    @FXML
    private void handleMenuItemRoomDetails(final ActionEvent event) {
	Platform.runLater(() -> showRoomDetailsDialog());
    }

    @FXML
    private void handleMenuItemSave(final ActionEvent event) {
	save(SettingsManager.getInstance().getDataFile());
    }

    @FXML
    private void handleMenuItemSettingsColors(final ActionEvent event) {

    }

    @FXML
    private void handleMenuItemSettingsGeneral(final ActionEvent event) {
	Platform.runLater(() -> showSettingsGeneral());
    }

    @FXML
    private void handleMenuItemSettingsICal(final ActionEvent event) {
	Platform.runLater(() -> showSettingsICal());
    }

    @FXML
    private void handleMenuItemShowEarningsChart(final ActionEvent event) {
	Platform.runLater(() -> showEarningsChart());
    }

    @FXML
    private void handleMenuItemShowMonthlyMoney(final ActionEvent event) {
	Platform.runLater(() -> showMonthlyMoney());
    }

    @FXML
    private void handleMenuItemShowNightlyRateChart(final ActionEvent event) {
	Platform.runLater(() -> showNightlyRateChart());
    }

    @FXML
    private void handleMenuItemShowProfitChart(final ActionEvent event) {
	Platform.runLater(() -> showProfitChart());
    }

    @FXML
    private void handleMenuItemUpcomingEvents(final ActionEvent event) {
	Platform.runLater(() -> showUpcomingEvents());
    }

    @FXML
    private void handleMenuItemWriteToGoogleCalendar(final ActionEvent event) {
	final WriteToGoogleCalendarService s = new WriteToGoogleCalendarService();
	s.start();

    }

    private void handleTableSelectEvent(final MouseEvent event) {
	Platform.runLater(() -> showRoomDetailsDialog());
	Platform.runLater(() -> showBookingDetails());
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	guestNameFilterInput.textProperty().addListener((observable, oldValue, newValue) -> {
	    tableView.getSelectionModel().clearSelection();
	    manager.applyFilter(newValue);
	});
	initTableView();

	// clear selection when settings changed, otherwise app. will die
	SettingsManager.getInstance().showNetEarningsProperty()
		.addListener(c -> tableView.getSelectionModel().clearSelection());

	tableView.getSelectionModel().getSelectedCells().addListener(getCellSelectionListener());
	tableView.setOnMousePressed(event -> {
	    if (event.isPrimaryButtonDown() && (event.getClickCount() == 2)) {
		handleTableSelectEvent(event);
	    }
	});
	tableView.setRowFactory(getRowFactory());

	tableView.setOnSort(event -> {

	    tableView.getSelectionModel().clearSelection();

	});

	initStatusLabelListeners();

    }

    private void initStatusLabelListeners() {
	// listen for data changes
	manager.getUIData().addListener((ListChangeListener<DateBean>) c -> {
	    updateStatusLabel();
	});
	// listen for selection changes
	RoomBeanSelectionManager.getInstance().selectionProperty().addListener((ListChangeListener<RoomBean>) c -> {
	    updateStatusLabel();
	});
	// listen for settings changes
	SettingsManager.getInstance().completePaymentProperty().addListener(c -> {
	    updateStatusLabel();
	});
	SettingsManager.getInstance().cleaningFeesProperty().addListener(c -> {
	    updateStatusLabel();
	});

    }

    private void initTableView() {

	setTableColumns();

	tableView.getSelectionModel().getSelectedCells().addListener(
		(@SuppressWarnings("rawtypes") final ListChangeListener.Change<? extends TablePosition> c) -> {
		    rowsWithSelectedCells.clear();
		    final Set<Integer> rows = tableView.getSelectionModel().getSelectedCells().stream()
			    .map(pos -> pos.getRow()).collect(Collectors.toSet());
		    rowsWithSelectedCells.addAll(rows);
		});

	// tableView.setItems(dataModel.getData());
	tableView.setItems(uiData.datesProperty());
	tableView.getSelectionModel().setCellSelectionEnabled(true);
	tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

	initTableViewContextMenus();

    }

    private void initTableViewContextMenus() {
	final ContextMenu menu = new ContextMenu();
	final MenuItem deleteItem = new MenuItem("Delete");
	deleteItem.setAccelerator(KeyCombination.keyCombination("Shortcut+D"));
	final MenuItem addBookingEvent = new MenuItem("Add Booking");
	addBookingEvent.setAccelerator(KeyCombination.keyCombination("Shortcut+A"));
	final MenuItem addCleaningEvent = new MenuItem("Add Cleaning");
	addCleaningEvent.setAccelerator(KeyCombination.keyCombination("Shortcut+C"));
	final MenuItem mi3 = new MenuItem("Modify");
	deleteItem.setOnAction(event -> {
	    Platform.runLater(() -> deleteSelected());
	});
	addBookingEvent.setOnAction(event -> {
	    Platform.runLater(() -> addBooking());
	});
	addCleaningEvent.setOnAction(event -> {
	    Platform.runLater(() -> addCleaning());
	});
	mi3.setOnAction(event -> {
	    Platform.runLater(() -> showModifyBookingDialog());
	});
	menu.getItems().addAll(addBookingEvent, addCleaningEvent, deleteItem, mi3);

	tableView.setContextMenu(menu);
	tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
	    if (t.getButton() == MouseButton.SECONDARY) {
		menu.show(tableView, t.getScreenX(), t.getScreenY());
	    }
	});
    }

    private void openFile() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Opening DrBookings xml");
	}
	final FileChooser fileChooser = new FileChooser();
	final File file = SettingsManager.getInstance().getDataFile();
	fileChooser.setInitialDirectory(file.getParentFile());
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("Dr.BookingBean BookingBean Data", Arrays.asList("*.xml", "*.XML")),
		new FileChooser.ExtensionFilter("All Files", "*"));
	fileChooser.setTitle("Open Resource File");
	fileChooser.setInitialFileName(file.getName());
	final File file2 = fileChooser.showOpenDialog(node.getScene().getWindow());
	if (file2 != null) {
	    readDataFile(file2);
	}
    }

    @Deprecated
    public void readDataFile(final File file) {
	new OpenFileService(file).start();
    }

    public void save(final File file) {
	new SaveService(file).start();
    }

    void scrollToToday() {
	final int index = uiData.getDates().indexOf(new DateBean(LocalDate.now()));
	if (index >= 0) {
	    if (logger.isDebugEnabled()) {
		logger.debug("Scrolling to index " + index);
	    }
	    tableView.scrollTo(index);

	} else if (logger.isDebugEnabled()) {
	    logger.debug("no entry for today");
	}
    }

    private void selectCurrentMonthFX() {

	setWorking(true);
	tableView.sort();
	final int[] currentMonthIndices = getCurrentMonthIndicies();
	tableView.getSelectionModel().selectIndices(currentMonthIndices[0], currentMonthIndices);
	if (logger.isDebugEnabled()) {
	    logger.debug("Selected dates: " + tableView.getSelectionModel().getSelectedItems().size());
	}
	setWorking(false);
    }

    private void selectLastMonthFX() {

	setWorking(true);
	tableView.sort();
	final int[] currentMonthIndices = getLastMonthIndicies();
	tableView.getSelectionModel().selectIndices(currentMonthIndices[0], currentMonthIndices);
	setWorking(false);
    }

    private void selectLastThreeMonthFX() {

	setWorking(true);
	tableView.sort();
	final int[] currentMonthIndices = getLastThreeMonthIndicies();
	tableView.getSelectionModel().selectIndices(currentMonthIndices[0], currentMonthIndices);
	setWorking(false);
    }

    private void selectNextMonthFX() {

	setWorking(true);
	final int[] monthIndices = getNextMonthIndicies(YearMonth.from(getSelectedDate()));
	tableView.sort();
	tableView.getSelectionModel().selectIndices(monthIndices[0], monthIndices);
	setWorking(false);
    }

    private void selectPrevMonthFX() {

	setWorking(true);
	final int[] monthIndices = getPrevMonthIndicies(YearMonth.from(getSelectedDate()));
	tableView.sort();
	tableView.getSelectionModel().selectIndices(monthIndices[0], monthIndices);
	setWorking(false);
    }

    private void setTableColumns() {
	addDateColumn();
	final int numberRooms = SettingsManager.getInstance().getNumberOfRooms();
	final String prefix = SettingsManager.getInstance().getRoomNamePrefix();
	for (int i = 1; i <= numberRooms; i++) {
	    final TableColumn<DateBean, DateBean> col1 = new TableColumn<>(prefix + i);
	    col1.setCellValueFactory(new PropertyValueFactory<>("self"));
	    col1.setCellFactory(new StudioCellFactory("" + i));
	    tableView.getColumns().add(col1);
	}
	// addOccupancyRateColumn();
	// addEarningsColumn();
    }

    private void setWorking(final boolean working) {
	Platform.runLater(() -> setWorkingFX(working, true));
    }

    private void setWorking(final boolean working, final boolean success) {
	Platform.runLater(() -> setWorkingFX(working, success));
    }

    private void setWorkingFX(final boolean working, final boolean success) {
	// if (!working) {
	// return;
	// }
	buttonAddBooking.getScene().getRoot().setCursor(Cursor.WAIT);
	progressBar.setVisible(working);
	if (success) {
	    progressLabel.setVisible(working);
	    progressLabel.getStyleClass().remove("warning");
	} else {
	    progressLabel.getStyleClass().add("warning");
	}
	buttonAddBooking.setDisable(working);
	buttonGoHome.setDisable(working);
	// buttonSelectCurrentMonth.setDisable(working);
	// buttonSelectLastMonth.setDisable(working);
	// buttonSelectLastThreeMonth.setDisable(working);
	// buttonSelectPrevMonth.setDisable(working);
	// buttonSelectNextMonth.setDisable(working);
	clearFilterButton.setDisable(working);

	filterBookingsLabel.setDisable(working);
	guestNameFilterInput.setDisable(working);
	tableView.setDisable(working);
	statusLabel.setDisable(working);
	guestNameFilterInput.setDisable(working);
	progressLabel.textProperty().unbind();
	progressBar.getScene().getRoot().setCursor(Cursor.DEFAULT);

    }

    private void showAbout() {

	final Alert alert = new Alert(AlertType.INFORMATION);
	alert.setTitle("About DrBookings");
	final TextArea label = new TextArea();

	label.setEditable(false);
	label.setPrefHeight(400);
	label.setPrefWidth(400);
	label.getStyleClass().add("copyable-label");

	final StringBuilder sb = new StringBuilder();

	try {
	    sb.append("Application version\t").append(Manifests.read("Implementation-Version")).append("\n");
	    sb.append("Build time\t\t").append(Manifests.read("Build-Time")).append("\n");
	} catch (final Exception e) {
	    if (logger.isInfoEnabled()) {
		logger.error("Failed to add manifest entry", e.getLocalizedMessage());
	    }
	}

	label.setText(sb.toString());
	alert.setHeaderText("proudly brought to you by kerner1000");
	alert.setContentText(null);
	alert.getDialogPane().setContent(label);
	alert.showAndWait();
    }

    private void showAddBookingDialog() {
	showAddBookingDialog(null, null);
    }

    public UIData getUIData() {
	return uiData;
    }

    private void showAddCleaningDialog(final LocalDate date, final String roomName) {
	try {
	    final Stage stage = FXUIUtils.buildStageFromFxml(getClass().getResource("/fxml/AddCleaningView.fxml"),
		    "Add cleaning", cleaningDialogWidth, cleaningDialogHeight);
	    final Stage windowStage = (Stage) node.getScene().getWindow();
	    stage.initOwner(windowStage);
	    stage.initModality(Modality.WINDOW_MODAL);
	    FXUIUtils.centerStageOnScreen(stage, node);
	    stage.show();
	} catch (final IOException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
    }

    private void showAddBookingDialog(final LocalDate date, final String roomName) {
	try {
	    final Pair<Stage, FXMLLoader> stageAndLoader = FXUIUtils
		    .buildStageFromFxml2(getClass().getResource("/fxml/AddBookingView.fxml"), "Add Booking", 300, 600);
	    final Stage stage = stageAndLoader.getLeft();
	    final FXMLLoader loader = stageAndLoader.getRight();
	    final AddBookingController c = loader.getController();
	    c.setManager(manager);
	    c.datePickerCheckIn.setValue(date);
	    c.comboBoxRoom.getSelectionModel().select(roomName);
	    final Stage windowStage = (Stage) node.getScene().getWindow();
	    stage.initOwner(windowStage);
	    stage.initModality(Modality.WINDOW_MODAL);
	    FXUIUtils.centerStageOnScreen(stage, node);
	    stage.show();
	} catch (final IOException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
    }

    void showBookingDetails() {
	if (bookingDetailsDialogFactory == null) {
	    bookingDetailsDialogFactory = new BookingDetailsDialogFactory(getManager());
	}
	bookingDetailsDialogFactory.showDialog();
    }

    private void showCleaningPlan() {

	// final Alert alert = new Alert(AlertType.INFORMATION);
	// alert.setTitle("Cleaning Plan");
	// final TextArea label = new TextArea();
	// label.setEditable(false);
	// label.setPrefHeight(400);
	// label.setPrefWidth(400);
	// label.getStyleClass().add("copyable-label");
	// label.setText(new
	// CleaningPlan(manager.getCleaningEntries()).toString());
	// alert.setHeaderText("Cleaning Plan");
	// alert.setContentText(null);
	// alert.getDialogPane().setContent(label);
	// alert.show();

	new CleaningPlanDialogFactory(getManager()).showDialog();
    }

    private void showEarningsChart() {
	if (earningsChartFactory == null) {
	    earningsChartFactory = new EarningsChartFactory();
	}
	earningsChartFactory.showDialog();
    }

    private void showEarningsView() {
	if (earningsViewFactory == null) {
	    earningsViewFactory = new EarningsViewFactory(getManager());
	}
	earningsViewFactory.showDialog();
    }

    void showModifyBookingDialog() {

	if (modifyBookingDialogFactory == null) {
	    modifyBookingDialogFactory = new ModifyBookingDialogFactory(getManager());
	}
	modifyBookingDialogFactory.showDialog();
    }

    private void showMonthlyMoney() {
	if (monthlyMoneyFactory == null) {
	    monthlyMoneyFactory = new StatisticsFactory(getManager());
	}
	monthlyMoneyFactory.showDialog();
    }

    private void showNightlyRateChart() {
	nightlyRateChartFactory = new NightlyRateChartFactory();
	nightlyRateChartFactory.showDialog();
    }

    private void showProfitChart() {
	if (profitChartFactory == null) {
	    profitChartFactory = new ProfitChartFactory();
	}
	profitChartFactory.showDialog();
    }

    private void showRoomDetailsDialog() {
	if (roomDetailsDialogFactory == null) {
	    roomDetailsDialogFactory = new RoomDetailsDialogFactory(this);
	}
	roomDetailsDialogFactory.showDialog();

    }

    private void showSettingsGeneral() {
	if (generalSettingsDialogFactory == null) {
	    generalSettingsDialogFactory = new GeneralSettingsDialogFactory();
	}
	generalSettingsDialogFactory.showDialog();
    }

    private void showSettingsICal() {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ICalSettingsView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root);
	    stage.setTitle("iCal Settings");
	    stage.setScene(scene);
	    stage.setWidth(600);
	    stage.setHeight(400);
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    private void showUpcomingEvents() {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UpcomingView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root);
	    stage.setTitle("What's next");
	    stage.setScene(scene);
	    stage.setWidth(600);
	    stage.setHeight(400);
	    final Stage windowStage = (Stage) node.getScene().getWindow();
	    stage.setX((windowStage.getX() + (windowStage.getWidth() / 2)) - (stage.getWidth() / 2));
	    stage.setY(((windowStage.getY() + windowStage.getHeight()) / 2) - (stage.getHeight() / 2));
	    final UpcomingController c = loader.getController();
	    c.setManager(getManager());
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    public void shutDown() {
	if (logger.isInfoEnabled()) {
	    logger.info("Shutting down");
	}
	EXECUTOR.shutdown();
	try {
	    EXECUTOR.awaitTermination(DEFAULT_THREAD_WAIT_SECONDS, TimeUnit.SECONDS);
	} catch (final InterruptedException e) {
	    if (logger.isErrorEnabled()) {
		logger.error(e.getLocalizedMessage(), e);
	    }
	}
    }

    private void updateStatusLabel() {
	Platform.runLater(() -> updateStatusLabelFX());

    }

    private void updateStatusLabelFX() {

	final ObservableList<RoomBean> selectedRooms = RoomBeanSelectionManager.getInstance().selectionProperty();

	final List<BookingEntry> selectedBookings = selectedRooms.stream().filter(r -> r.getBookingEntry() != null)
		.flatMap(r -> r.getBookingEntry().toList().stream()).collect(Collectors.toList());
	final Range<LocalDate> selectedRange = DateBeanSelectionManager.getInstance().getSelectedDateRange();
	if (selectedRange == null) {

	} else {
	    final StringBuilder sb = new StringBuilder();
	    sb.append(selectedRange.lowerEndpoint());
	    sb.append(" ➜ ");
	    sb.append(selectedRange.upperEndpoint());
	    selectedDatesLabel.setText(sb.toString());
	}

	final BookingsByOrigin<BookingEntry> bo = new BookingsByOrigin<>(selectedBookings);
	final StringBuilder sb = new StringBuilder(new StatusLabelStringFactory(bo).build());

	// sb.append("\tPerformance total:" +
	// StatusLabelStringFactory.DECIMAL_FORMAT.format(pc.getProfit()) + "
	// \t"
	// + "Performance/hour:" +
	// StatusLabelStringFactory.DECIMAL_FORMAT.format(pc.getProfitPerHour()));
	statusLabel.textProperty().set(sb.toString());
    }

}

package com.github.drbookings.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.DrBookingsApplication;
import com.github.drbookings.LocalDates;
import com.github.drbookings.google.GoogleCalendarSync;
import com.github.drbookings.ical.AirbnbICalParser;
import com.github.drbookings.ical.ICalBookingFactory;
import com.github.drbookings.ical.XlsxBookingFactory;
import com.github.drbookings.model.MinimumPriceProvider;
import com.github.drbookings.model.OccupancyRateProvider;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.UnmarshallListener;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.BookingFilter;
import com.github.drbookings.ui.BookingReaderService;
import com.github.drbookings.ui.BookingsByOrigin;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.OccupancyCellFactory;
import com.github.drbookings.ui.OccupancyCellValueFactory;
import com.github.drbookings.ui.StatusLabelStringFactory;
import com.github.drbookings.ui.StudioCellFactory;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.dialogs.BookingDetailsDialogFactory;
import com.github.drbookings.ui.dialogs.CleaningPlanDialogFactory;
import com.github.drbookings.ui.dialogs.GeneralSettingsDialogFactory;
import com.github.drbookings.ui.dialogs.RoomDetailsDialogFactory;
import com.jcabi.manifests.Manifests;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

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

    private abstract class DrBookingService<T> extends Service<T> {
	public DrBookingService() {
	    setOnScheduled(e -> {
		setWorking(true);
		progressLabel.setText(null);
	    });
	    setOnRunning(e -> {
		progressLabel.setText("Working..");
	    });
	    setOnFailed(e -> {
		setWorking(false, false);
		final Throwable t = getException();
		if (logger.isErrorEnabled()) {
		    logger.error(t.getLocalizedMessage(), t);
		}
		progressLabel.setText("Error: " + t);
	    });
	    setOnSucceeded(e -> {
		setWorking(false);
		progressLabel.setText(null);
	    });
	}
    }

    private class OpenFileService extends DrBookingService<DataStore> {

	private final File file;

	private final UnmarshallListener l;

	public OpenFileService(final File file) {
	    super();
	    this.file = file;
	    this.l = new UnmarshallListener();

	    setOnScheduled(e -> {
		setWorking(true);
		progressLabel.textProperty()
			.bind(Bindings.createObjectBinding(buildProgressString(l), l.bookingCountProperty()));
	    });

	    setOnSucceeded(e -> {
		progressLabel.textProperty().unbind();
		progressLabel.setText("Rendering..");
		try {
		    getValue().load(getManager());
		    scrollToToday();
		} catch (final Exception e1) {
		    logger.error(e1.getLocalizedMessage(), e1);
		    progressLabel.setText("Error: " + e1);
		}
		progressLabel.setText(null);
		setWorking(false);
	    });

	}

	@Override
	protected Task<DataStore> createTask() {
	    return new Task<DataStore>() {

		@Override
		protected DataStore call() throws Exception {
		    SettingsManager.getInstance().setDataFile(file);
		    final DataStore ds = new XMLStorage().setListener(l).load(file);
		    return ds;
		};
	    };
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

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.00");

    @FXML
    private Node node;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label filterBookingsLabel;

    @FXML
    private TableView<DateBean> tableView;

    @FXML
    private Label statusLabel;

    @FXML
    private MenuItem menuItemExit;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    private Button buttonAddBooking;

    @FXML
    private Button buttonGoHome;

    @FXML
    private Button buttonSelectCurrentMonth;

    @FXML
    private Button buttonSelectLastThreeMonth;

    @FXML
    private Button buttonSelectLastMonth;

    @FXML
    private TextField guestNameFilterInput;

    private final ObservableSet<Integer> rowsWithSelectedCells = FXCollections.observableSet();

    private final MainManager manager;

    private BookingDetailsDialogFactory bookingDetailsDialogFactory;

    private GeneralSettingsDialogFactory generalSettingsDialogFactory;

    @FXML
    private OverviewChartController overviewChartViewController;

    private RoomDetailsDialogFactory roomDetailsDialogFactory;

    private final OccupancyRateProvider occupancyRateProvider = new OccupancyRateProvider();

    private final MinimumPriceProvider minimumPriceProvider = new MinimumPriceProvider();

    public MainController() {
	manager = new MainManager();
    }

    private void addBooking() {
	final ObservableList<RoomBean> dates = CellSelectionManager.getInstance().getSelection();
	Platform.runLater(() -> showAddBookingDialog(dates.get(0).getDate(), dates.get(0).getName()));
    }

    private void addDateColumn() {
	final TableColumn<DateBean, LocalDate> col = new TableColumn<>("Date");
	col.setCellValueFactory(new PropertyValueFactory<DateBean, LocalDate>("date"));
	col.setCellFactory(column -> {
	    return new TableCell<DateBean, LocalDate>() {
		@Override
		protected void updateItem(final LocalDate item, final boolean empty) {
		    super.updateItem(item, empty);
		    if (item == null || empty) {
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
	final TableColumn<DateBean, Number> col = new TableColumn<>("Av.Earnings");
	col.setCellValueFactory(new PropertyValueFactory<DateBean, Number>("totalEarnings"));
	col.setCellFactory(column -> {
	    return new TableCell<DateBean, Number>() {

		@Override
		protected void updateItem(final Number item, final boolean empty) {
		    super.updateItem(item, empty);
		    if (item == null || empty) {
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

    @SuppressWarnings({ "unchecked", "rawtypes" })
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
		    final RoomBean rb = db.getRoom("" + c);
		    if (rb.isEmpty()) {
			continue;
		    }
		    final Booking booking = rb.getFilteredBookingEntries().get(0).getElement();
		    if (logger.isDebugEnabled()) {
			logger.debug("Deleting " + booking);
			manager.removeBooking(booking);
		    }
		}
	    }
	}
    }

    @SuppressWarnings("rawtypes")
    private ListChangeListener<TablePosition> getCellSelectionListener() {
	return change -> {
	    final List<RoomBean> cells = new ArrayList<>();
	    for (final TablePosition tp : change.getList()) {
		final int r = tp.getRow();
		final int c = tp.getColumn();
		final Object cell = tp.getTableColumn().getCellData(r);
		// if (logger.isDebugEnabled()) {
		// logger.debug("Selection changed to " + cell);
		// }
		if (cell instanceof DateBean) {
		    final RoomBean room = ((DateBean) cell).getRoom("" + c);
		    if (room != null) {
			cells.add(room);
		    }
		    // else {
		    // System.err.println(cell);
		    // }
		}
	    }
	    // System.err.println(cells);
	    CellSelectionManager.getInstance().setSelection(cells);
	};
    }

    private int[] getCurrentMonthIndicies() {
	final List<Integer> result = new ArrayList<>();
	for (int index = 0; index < tableView.getItems().size(); index++) {
	    final LocalDate date = tableView.getItems().get(index).getDate();
	    if (LocalDates.isCurrentMonth(date)) {
		result.add(index);
	    }
	}
	if (result.isEmpty()) {
	    return new int[0];
	}
	return ArrayUtils.toPrimitive(result.toArray(new Integer[] { result.size() }));

    }

    public String getGuestNameFilter() {
	return guestNameFilterInput.getText();
    }

    private int[] getLastMonthIndicies() {
	final List<Integer> result = new ArrayList<>();
	for (int index = 0; index < tableView.getItems().size(); index++) {
	    final LocalDate date = tableView.getItems().get(index).getDate();
	    if (LocalDates.isLastMonth(date)) {
		result.add(index);
	    }
	}
	return ArrayUtils.toPrimitive(result.toArray(new Integer[] { result.size() }));

    }

    private int[] getLastThreeMonthIndicies() {
	final List<Integer> result = new ArrayList<>();
	for (int index = 0; index < tableView.getItems().size(); index++) {
	    final LocalDate date = tableView.getItems().get(index).getDate();
	    if (LocalDates.isLastThreeMonths(date)) {
		result.add(index);
	    }
	}
	return ArrayUtils.toPrimitive(result.toArray(new Integer[] { result.size() }));

    }

    public MainManager getManager() {
	return manager;
    }

    private Callback<TableView<DateBean>, TableRow<DateBean>> getRowFactory() {
	return param -> {

	    final TableRow<DateBean> row = new TableRow<DateBean>() {
		@Override
		protected void updateItem(final DateBean item, final boolean empty) {
		    super.updateItem(item, empty);
		    getStyleClass().removeAll("now", "end-of-month");
		    if (empty || item == null) {

		    } else {
			if (item.getDate().isEqual(LocalDate.now())) {
			    getStyleClass().add("now");
			} else if (item.getDate()
				.equals(item.getDate().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()))) {
			    getStyleClass().add("end-of-month");
			} else {

			}
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

    @FXML
    private void handleButtonAddBooking(final ActionEvent event) {
	Platform.runLater(() -> showAddBookingDialog());
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
    private void handleMenuItemAbout(final ActionEvent event) {
	Platform.runLater(() -> showAbout());
    }

    @FXML
    private void handleMenuItemBookingDetails(final ActionEvent event) {
	Platform.runLater(() -> showBookingDetails());
    }

    @FXML
    private void handleMenuItemCleaningPlan(final ActionEvent event) {
	Platform.runLater(() -> showCleaningPlan());

    }

    @FXML
    private void handleMenuItemClearData(final ActionEvent event) {
	Platform.runLater(() -> clearData());
    }

    @FXML
    private void handleMenuItemClearGoogleCalendar(final ActionEvent event) {
	final ClearGoogleCalendarService s = new ClearGoogleCalendarService();
	s.start();
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
	    logger.debug("Opening Booking Excel");
	}
	final FileChooser fileChooser = new FileChooser();
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("Excel", Arrays.asList("*.xls", "*.XLS")),
		new FileChooser.ExtensionFilter("All Files", "*"));
	fileChooser.setTitle("Open Booking Excel");
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

	overviewChartViewController.setMainController(this);

	guestNameFilterInput.textProperty().addListener(
		(ChangeListener<String>) (observable, oldValue, newValue) -> manager.applyFilter(newValue));
	initTableView();

	tableView.getSelectionModel().getSelectedCells().addListener(getCellSelectionListener());
	tableView.setOnMousePressed(event -> {
	    if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
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
	CellSelectionManager.getInstance().getSelection().addListener((ListChangeListener<RoomBean>) c -> {
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
	tableView.setItems(manager.getUIData());
	tableView.getSelectionModel().setCellSelectionEnabled(true);
	tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	initTableViewContextMenus();

    }

    private void initTableViewContextMenus() {
	final ContextMenu menu = new ContextMenu();
	final MenuItem mi1 = new MenuItem("Delete");
	final MenuItem mi2 = new MenuItem("Add");
	mi1.setOnAction(event -> {
	    Platform.runLater(() -> deleteSelected());
	});
	mi2.setOnAction(event -> {
	    Platform.runLater(() -> addBooking());
	});
	menu.getItems().addAll(mi2, mi1);

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
		new FileChooser.ExtensionFilter("Dr.Booking Booking Data", Arrays.asList("*.xml", "*.XML")),
		new FileChooser.ExtensionFilter("All Files", "*"));
	fileChooser.setTitle("Open Resource File");
	fileChooser.setInitialFileName(file.getName());
	final File file2 = fileChooser.showOpenDialog(node.getScene().getWindow());
	if (file2 != null) {
	    readDataFile(file2);

	}
    }

    public void readDataFile(final File file) {
	new OpenFileService(file).start();
    }

    void scrollToToday() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Trying to scroll to today");
	}
	final int index = getManager().getUIData().indexOf(new DateBean(LocalDate.now(), getManager()));
	if (index >= 0) {
	    if (logger.isDebugEnabled()) {
		logger.debug("Scrolling to index " + index);
	    }
	    tableView.scrollTo(index);

	} else {
	    if (logger.isDebugEnabled()) {
		logger.debug("no entry for today");
	    }
	}
    }

    private void selectCurrentMonthFX() {

	setWorking(true);
	tableView.sort();
	final int[] currentMonthIndices = getCurrentMonthIndicies();
	tableView.getSelectionModel().selectIndices(currentMonthIndices[0], currentMonthIndices);
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

    private void setTableColumns() {
	addDateColumn();
	final int numberRooms = SettingsManager.getInstance().getNumberOfRooms();
	final String prefix = SettingsManager.getInstance().getRoomNamePrefix();
	for (int i = 1; i <= numberRooms; i++) {
	    final TableColumn<DateBean, DateBean> col1 = new TableColumn<>(prefix + i);
	    col1.setCellValueFactory(new PropertyValueFactory<DateBean, DateBean>("self"));
	    col1.setCellFactory(new StudioCellFactory("" + i));
	    tableView.getColumns().add(col1);
	}
	addOccupancyRateColumn();
	addEarningsColumn();
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
	buttonSelectCurrentMonth.setDisable(working);
	buttonSelectLastMonth.setDisable(working);
	buttonSelectLastThreeMonth.setDisable(working);
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

    private void showAddBookingDialog(final LocalDate date, final String roomName) {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddBookingView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    stage.setWidth(300);
	    stage.setHeight(400);
	    final Scene scene = new Scene(root);
	    stage.setTitle("Add Booking");
	    stage.setScene(scene);
	    final AddBookingController c = loader.getController();
	    c.setManager(manager);
	    c.datePickerCheckIn.setValue(date);
	    c.comboBoxRoom.getSelectionModel().select(roomName);
	    final Stage windowStage = (Stage) node.getScene().getWindow();
	    stage.initOwner(windowStage);
	    stage.initModality(Modality.WINDOW_MODAL);
	    stage.setX(windowStage.getX() + windowStage.getWidth() / 2 - stage.getWidth() / 2);
	    stage.setY((windowStage.getY() + windowStage.getHeight()) / 2 - stage.getHeight() / 2);
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    private void showBookingDetails() {
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

    private void showRoomDetailsDialog() {
	if (this.roomDetailsDialogFactory == null) {
	    this.roomDetailsDialogFactory = new RoomDetailsDialogFactory(getManager());
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
	    stage.setX(windowStage.getX() + windowStage.getWidth() / 2 - stage.getWidth() / 2);
	    stage.setY((windowStage.getY() + windowStage.getHeight()) / 2 - stage.getHeight() / 2);
	    final UpcomingController c = loader.getController();
	    c.setManager(getManager());
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    public void shutDown() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Shutting down");
	}

    }

    private void updateStatusLabel() {
	Platform.runLater(() -> updateStatusLabelFX());

    }

    private void updateStatusLabelFX() {

	final ObservableList<RoomBean> selectedRooms = CellSelectionManager.getInstance().getSelection();
	final List<BookingEntry> selectedBookings = selectedRooms.stream().flatMap(r -> r.getBookingEntries().stream())
		.filter(new BookingFilter(guestNameFilterInput.getText())).collect(Collectors.toList());
	final BookingsByOrigin bo = new BookingsByOrigin(selectedBookings);
	final StringBuilder sb = new StringBuilder(new StatusLabelStringFactory(bo).build());
	sb.append("\tOccupancyRate:");
	sb.append(StatusLabelStringFactory.DECIMAL_FORMAT.format(occupancyRateProvider.getOccupancyRate() * 100));
	sb.append("%\tMinPriceAtRate:");
	sb.append(StatusLabelStringFactory.DECIMAL_FORMAT.format(minimumPriceProvider.getMinimumPrice()));
	sb.append("€");
	statusLabel.textProperty().set(sb.toString());
    }

}

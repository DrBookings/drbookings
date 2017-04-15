package com.github.drbookings.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.OptionalDouble;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.Booking;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ser.UnmarshallListener;
import com.github.drbookings.ser.XMLStorage;
import com.github.drbookings.ui.BookingFilter;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.OccupancyCellFactory;
import com.github.drbookings.ui.OccupancyCellValueFactory;
import com.github.drbookings.ui.StudioCellFactory;
import com.github.drbookings.ui.beans.DateBean;
import com.github.drbookings.ui.beans.RoomBean;
import com.jcabi.manifests.Manifests;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final String defaultDataFileNameKey = "data";

    private static final String defaultDataFileName = "booking-data.xml";

    private static final DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, E");

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.00");

    private static String getStatusLabelString(final Collection<BookingEntry> bookingBookings,
	    final Collection<BookingEntry> airbnbBookings, final Collection<BookingEntry> otherBookings) {
	final StringBuilder sb = new StringBuilder();
	sb.append("Airbnb nights: ");
	sb.append(airbnbBookings.stream().filter(b -> !b.isCheckOut()).count());
	sb.append(" (");
	// we want total payment, since payment is done once
	final Set<Booking> airbnbBookings2 = airbnbBookings.stream().map(b -> b.getElement())
		.collect(Collectors.toSet());
	sb.append(String.format("%6.2f€", airbnbBookings2.stream().mapToDouble(b -> b.getNetEarnings()).sum()));
	sb.append(")");
	sb.append(", Booking nights: ");
	sb.append(bookingBookings.stream().filter(b -> !b.isCheckOut()).count());
	sb.append(" (");
	// we want total payment, since payment is done once
	final Set<Booking> bookingBookings2 = bookingBookings.stream().map(b -> b.getElement())
		.collect(Collectors.toSet());
	sb.append(String.format("%6.2f€", bookingBookings2.stream().mapToDouble(b -> b.getNetEarnings()).sum()));
	sb.append(")");
	sb.append(", Other nights: ");
	sb.append(otherBookings.stream().filter(b -> !b.isCheckOut()).count());
	sb.append(" (");
	// we want total payment, since payment is done once
	final Set<Booking> otherBookings2 = otherBookings.stream().map(b -> b.getElement()).collect(Collectors.toSet());
	sb.append(String.format("%6.2f€", otherBookings2.stream().mapToDouble(b -> b.getNetEarnings()).sum()));
	sb.append(")");
	sb.append(", Total nights: ");
	sb.append(
		Stream.concat(bookingBookings.stream(), Stream.concat(airbnbBookings.stream(), otherBookings.stream()))
			.filter(b -> !b.isCheckOut()).count());
	sb.append(", Av. Net Earnings / Day: ");
	final OptionalDouble av = Stream
		.concat(bookingBookings.stream(), Stream.concat(airbnbBookings.stream(), otherBookings.stream()))
		.mapToDouble(b -> b.getNetEarnings()).average();
	if (av.isPresent()) {
	    sb.append(String.format("%3.2f€", av.getAsDouble()));
	} else {
	    sb.append(String.format("%3.2f€", 0.0));
	}
	return sb.toString();
    }

    @FXML
    private Node node;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private TableView<DateBean> tableView;

    @FXML
    private TableColumn<DateBean, LocalDate> cDate;

    @FXML
    private TableColumn<DateBean, DateBean> cStudio1;

    @FXML
    private TableColumn<DateBean, DateBean> cStudio2;

    @FXML
    private Label statusLabel;

    @FXML
    private TableColumn<DateBean, DateBean> cStudio3;

    @FXML
    private TableColumn<DateBean, DateBean> cStudio4;

    @FXML
    private TableColumn<DateBean, Number> cAuslastung;

    @FXML
    private TableColumn<DateBean, Number> cBruttoEarningsPerNightTotal;

    @FXML
    private MenuItem menuItemExit;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    private Button buttonAddBooking;

    @FXML
    private TextField guestNameFilterInput;

    private final ObservableSet<Integer> rowsWithSelectedCells = FXCollections.observableSet();

    private File file;

    private final MainManager manager;

    public MainController() {
	manager = new MainManager();
    }

    private void addBooking() {
	final ObservableList<RoomBean> dates = CellSelectionManager.getInstance().getSelection();
	Platform.runLater(() -> showAddBookingDialog(dates.get(0).getDate(), dates.get(0).getName()));
    }

    private Callable<String> buildProgressString(final UnmarshallListener listener) {
	return () -> "Bookings read: " + listener.getBookingCount();
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
		    final List<Booking> bookings = BookingEntry.getBookings(rb.getFilteredBookingEntries());
		    if (logger.isDebugEnabled()) {
			logger.debug("Deleting " + bookings);
			manager.removeBookings(bookings);
		    }
		}
	    }
	}
    }

    private void doUpdateStatusLabel() {

	final BookingsByOrigin bo = new BookingsByOrigin(manager.getBookingEntries().stream()
		.filter(new BookingFilter(guestNameFilterInput.getText())).collect(Collectors.toList()));
	statusLabel.textProperty()
		.set(getStatusLabelString(bo.getBookingBookings(), bo.getAirbnbBookings(), bo.getOtherBookings()));

    }

    @SuppressWarnings("rawtypes")
    private ListChangeListener<TablePosition> getCellSelectionListener() {
	return change -> {
	    final List<RoomBean> cells = new ArrayList<>();
	    for (final TablePosition tp : change.getList()) {
		final int r = tp.getRow();
		final int c = tp.getColumn();
		final Object cell = tp.getTableColumn().getCellData(r);
		if (logger.isDebugEnabled()) {
		    logger.debug("Selection changed to " + cell);
		}
		if (cell instanceof DateBean) {
		    final RoomBean room = ((DateBean) cell).getRoom("" + c);
		    if (room != null) {
			cells.add(room);
		    }
		}
	    }
	    CellSelectionManager.getInstance().setSelection(cells);
	};
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
    private void handleMenuItemAbout(final ActionEvent event) {
	Platform.runLater(() -> showAbout());
    }

    @FXML
    private void handleMenuItemCleaningPlan(final ActionEvent event) {
	Platform.runLater(() -> showCleaningPlan());

    }

    @FXML
    private void handleMenuItemOpen(final ActionEvent event) {
	if (logger.isDebugEnabled()) {
	    logger.debug("MenuItem Open");
	}
	final FileChooser fileChooser = new FileChooser();
	if (file != null) {
	    fileChooser.setInitialDirectory(file.getParentFile());
	}
	fileChooser.getExtensionFilters().addAll(
		new FileChooser.ExtensionFilter("Dr.Booking Booking Data", Arrays.asList("*.xml")),
		new FileChooser.ExtensionFilter("All Files", "*"));
	fileChooser.setTitle("Open Resource File");
	file = fileChooser.showOpenDialog(node.getScene().getWindow());

	if (file != null) {
	    try {
		final long elementsToRead = XMLStorage.countElements("booking", file);
		final UnmarshallListener l = new UnmarshallListener();
		progressBar.progressProperty().bind(l.bookingCountProperty().divide(elementsToRead));
		progressLabel.setText("Reading " + elementsToRead + " bookings..");
		new Thread(() -> {
		    try {
			setWorking(true);
			new XMLStorage(getManager()).setListener(l).load(file);
		    } catch (final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			UIUtils.showError(e);
		    } finally {
			setWorking(false);
		    }
		}).start();

	    } catch (final Exception e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	}

    }

    @FXML
    private void handleMenuItemSettingsColors(final ActionEvent event) {

    }

    @FXML
    private void handleMenuItemSettingsGeneral(final ActionEvent event) {
	Platform.runLater(() -> showSettingsGeneral());
    }

    private void handleTableSelectEvent(final MouseEvent event) {
	Platform.runLater(() -> showRoomDetailsDialog());
    }

    private void initDataFile() {
	final Preferences userPrefs = Preferences.userNodeForPackage(getClass());
	final String fileString = userPrefs.get(defaultDataFileNameKey,
		System.getProperty("user.home") + File.separator + defaultDataFileName);
	file = new File(fileString);

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

	guestNameFilterInput.textProperty().addListener(
		(ChangeListener<String>) (observable, oldValue, newValue) -> manager.applyGuestNameFilter(newValue));
	initTableView();
	cDate.setCellFactory(column -> {
	    return new TableCell<DateBean, LocalDate>() {
		@Override
		protected void updateItem(final LocalDate item, final boolean empty) {
		    super.updateItem(item, empty);

		    if (item == null || empty) {
			setText(null);
			setStyle("");
		    } else {
			setText(myDateFormatter.format(item));
		    }
		}
	    };
	});
	cBruttoEarningsPerNightTotal.setCellFactory(column -> {
	    return new TableCell<DateBean, Number>() {

		@Override
		protected void updateItem(final Number item, final boolean empty) {
		    super.updateItem(item, empty);
		    this.getStyleClass().removeAll("all-payed", "needs-payment");
		    if (item == null || empty) {
			setText(null);
		    } else {

			setText(decimalFormat.format(item));

		    }
		}

	    };
	});
	cDate.getStyleClass().addAll("center-left");
	cBruttoEarningsPerNightTotal.getStyleClass().add("opace");
	cStudio1.setCellFactory(new StudioCellFactory("1"));

	cStudio2.setCellFactory(new StudioCellFactory("2"));

	cStudio3.setCellFactory(new StudioCellFactory("3"));

	cStudio4.setCellFactory(new StudioCellFactory("4"));

	cAuslastung.setCellFactory(new OccupancyCellFactory());
	cAuslastung.setCellValueFactory(new OccupancyCellValueFactory());

	tableView.getSelectionModel().getSelectedCells().addListener(getCellSelectionListener());
	tableView.setOnMousePressed(event -> {
	    if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		handleTableSelectEvent(event);
	    }
	});
	tableView.setRowFactory(getRowFactory());
	manager.getUIData().addListener((ListChangeListener<DateBean>) c -> {
	    updateStatusLabel();
	});
	initDataFile();
    }

    private void initTableView() {
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

    private void setWorking(final boolean working) {
	Platform.runLater(() -> setWorkingFX(working));
    }

    private void setWorkingFX(final boolean working) {
	// if (!working) {
	// return;
	// }
	progressBar.setVisible(working);
	progressLabel.setVisible(working);
	buttonAddBooking.setDisable(working);
	guestNameFilterInput.setDisable(working);
    }

    private void showAbout() {

	final Alert alert = new Alert(AlertType.INFORMATION);
	alert.setTitle("About DrBookings");
	final TextArea label = new TextArea();

	label.setEditable(false);
	label.setPrefHeight(400);
	label.setPrefWidth(400);
	label.getStyleClass().add("copyable-label");

	label.setText(
		new StringBuilder().append("Application version\t").append(Manifests.read("Implementation-Version"))
			.append("\n").append("Build time\t").append(Manifests.read("Build-Time")).toString());
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
	    stage.setWidth(400);
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

    private void showCleaningPlan() {

	final Alert alert = new Alert(AlertType.INFORMATION);
	alert.setTitle("Cleaning Plan");
	final TextArea label = new TextArea();

	label.setEditable(false);
	label.setPrefHeight(400);
	label.setPrefWidth(400);
	label.getStyleClass().add("copyable-label");
	label.setText(new CleaningPlan(manager.getCleaningEntries()).toString());
	alert.setHeaderText("Cleaning Plan");
	alert.setContentText(null);
	alert.getDialogPane().setContent(label);
	alert.showAndWait();
    }

    private void showRoomDetailsDialog() {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoomDetailsView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root);
	    stage.setTitle("Room Details");
	    stage.setScene(scene);
	    stage.setWidth(600);
	    stage.setHeight(400);
	    final Stage windowStage = (Stage) node.getScene().getWindow();
	    stage.initOwner(windowStage);
	    stage.initModality(Modality.NONE);
	    stage.setX(windowStage.getX() + windowStage.getWidth() / 2 - stage.getWidth() / 2);
	    stage.setY((windowStage.getY() + windowStage.getHeight()) / 2 - stage.getHeight() / 2);
	    final RoomDetailsController c = loader.getController();
	    stage.setOnCloseRequest(event -> c.shutDown());
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    private void showSettingsGeneral() {

    }

    public void shutDown() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Shutting down");
	}

    }

    private void updateStatusLabel() {
	Platform.runLater(() -> doUpdateStatusLabel());

    }

}

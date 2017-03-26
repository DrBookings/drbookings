package com.github.drbookings.ui.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.github.drbookings.model.ModelConfiguration.NightCounting;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.BookingBeans;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.model.data.manager.BookingManager;
import com.github.drbookings.model.manager.DataModel;
import com.github.drbookings.model.manager.RoomManager;
import com.github.drbookings.ser.DataStore;
import com.github.drbookings.ser.MarshallListener;
import com.github.drbookings.ser.UnmarshallListener;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.OccupancyCellFactory;
import com.github.drbookings.ui.OccupancyCellValueFactory;
import com.github.drbookings.ui.StudioCellFactory;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
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
    private Label labelStatus;

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

    private final DataModel dataModel = new DataModel();

    private final BookingManager bookingManager = new BookingManager();

    private final RoomManager roomMananger = new RoomManager();

    private void addBooking() {
	final ObservableList<RoomBean> dates = CellSelectionManager.getInstance().getSelection();
	Platform.runLater(() -> showAddBookingDialog(dates.get(0).getDate()));
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
		if (((DateBean) cellData).getBookings().isEmpty()) {

		} else {
		    dataModel.removeAll(((DateBean) cellData).getRoom("" + c).get().getFilteredBookings().get(0));
		}
	    }
	}
    }

    private void doUpdateStatusLabel() {

	final long bookingDays = dataModel.getNumberOfBookingNights("(?i)booking");
	final double bookingEarnings = dataModel.getBruttoEarnings("(?i)booking");
	final long airbnbDays = dataModel.getNumberOfBookingNights("(?i)airbnb");
	final int distinctAirbnbStays = dataModel.getNumberOfDistinctBookings("(?i)airbnb");
	final int distinctBookingStays = dataModel.getNumberOfDistinctBookings("(?i)booking");
	final int distinctBookingOther = dataModel.getNumberOfDistinctBookings(BookingBeans.getRegexOther());
	final int distinctBookingsAll = dataModel.getNumberOfDistinctBookings();
	final double airbnbEarnings = dataModel.getBruttoEarnings("(?i)airbnb");
	final long otherDays = dataModel.getNumberOfBookingNights("(?!airbnb|booking)");
	final double otherEarnings = dataModel.getBruttoEarnings("(?!airbnb|booking)");
	labelStatus.setText("Booking: " + bookingDays + "/" + distinctBookingStays + " ("
		+ decimalFormat.format(bookingEarnings) + "€), Airbnb: " + airbnbDays + "/" + distinctAirbnbStays + " ("
		+ decimalFormat.format(airbnbEarnings) + "€), Other: " + otherDays + "/" + distinctBookingOther + " ("
		+ decimalFormat.format(otherEarnings) + "€), Total: " + (airbnbDays + bookingDays + otherDays) + "/"
		+ distinctBookingsAll + " (" + decimalFormat.format(airbnbEarnings + bookingEarnings + otherEarnings)
		+ "€)");
	labelStatus.setAlignment(Pos.CENTER);
	labelStatus.setStyle("-fx-font-weight: bold;");

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
		    final Optional<RoomBean> room = ((DateBean) cell).getRoom(c + "");
		    if (room.isPresent()) {
			cells.add(room.get());
		    }
		}
	    }
	    CellSelectionManager.getInstance().setSelection(cells);
	};
    }

    private String getCleaningPlanString(final Map<String, Multimap<LocalDate, String>> cleaningMap) {
	final StringBuilder sb = new StringBuilder();
	for (final Entry<String, Multimap<LocalDate, String>> e : cleaningMap.entrySet()) {
	    sb.append(e.getKey());
	    sb.append("\t");
	    for (final Iterator<Entry<LocalDate, Collection<String>>> eeIt = e.getValue().asMap().entrySet()
		    .iterator(); eeIt.hasNext();) {
		final Entry<LocalDate, Collection<String>> ee = eeIt.next();
		final LocalDate v = ee.getKey();
		sb.append(v.format(myDateFormatter));
		for (final Iterator<String> itS = ee.getValue().iterator(); itS.hasNext();) {
		    final String s = itS.next();
		    sb.append(",\tF");
		    sb.append(s);
		    if (itS.hasNext()) {
			sb.append(" ");
		    }
		}
		if (eeIt.hasNext()) {
		    sb.append("\n");
		    sb.append("\t\t");
		}

	    }
	    sb.append("\n");
	}
	return sb.toString();
    }

    private Callback<TableView<DateBean>, TableRow<DateBean>> getRowFactory() {
	return param -> {

	    final PseudoClass rowContainsSelectedCell = PseudoClass.getPseudoClass("contains-selection");

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
	    restoreState();
	}

    }

    @FXML
    private void handleMenuItemSettingsColors(final ActionEvent event) {

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
		(ChangeListener<String>) (observable, oldValue, newValue) -> dataModel.applyGuestNameFilter(newValue));
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

			if (this.getTableRow().getItem() != null && this.getTableRow().getItem() instanceof DateBean) {
			    final DateBean db = (DateBean) this.getTableRow().getItem();
			    if (db.getPaymentsReceived() >= db.getBookings().size()) {
				this.getStyleClass().add("all-payed");
				// System.err.println("all payed");
			    } else {
				this.getStyleClass().add("needs-payment");
			    }
			    final String percentString = String.format("%4.0f%%",
				    db.getPaymentsReceived() / +(float) db.getBookings().size() * 100);
			    setText(decimalFormat.format(item) + "\n" + percentString);
			} else {
			    setText(decimalFormat.format(item));
			}
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
	dataModel.getData().addListener((ListChangeListener<DateBean>) c -> {
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

	tableView.setItems(dataModel.getData());
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

    private void makeBackup() {
	if (file.exists() && file.length() != 0) {
	    try {
		FileUtils.copyFile(file, new File(file.getParentFile(), file.getName() + ".bak"));
	    } catch (final IOException e) {
		if (logger.isErrorEnabled()) {
		    logger.error(e.getLocalizedMessage(), e);
		}
	    }
	}
    }

    private void restoreState() {
	if (logger.isInfoEnabled()) {
	    logger.info("Loading state from " + file.getAbsolutePath());
	}

	final UnmarshallListener listener = new UnmarshallListener();

	progressLabel.textProperty()
		.bind(Bindings.createStringBinding(buildProgressString(listener), listener.bookingCountProperty()));

	new Thread(() -> {
	    try {
		setWorking(true);

		final long counter = countElements(file);

		if (logger.isDebugEnabled()) {
		    logger.debug(counter + " total elements to process");
		}

		Platform.runLater(() -> {
		    progressBar.progressProperty().bind(Bindings.createDoubleBinding(() -> {
			final double result = listener.getBookingCount() / counter;
			return result;
		    }, listener.bookingCountProperty()));
		});

		final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
		final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();

		jaxbMarshaller.setListener(listener);
		final List<BookingBean> data = ((DataStore) jaxbMarshaller.unmarshal(file)).getBookings();
		Platform.runLater(() -> {
		    progressLabel.textProperty().unbind();
		    progressLabel.textProperty().set("Rendering..");
		});
		Platform.runLater(() -> {
		    try {
			dataModel.setAll(data);
		    } catch (final Exception e) {
			if (logger.isErrorEnabled()) {
			    logger.error(e.getLocalizedMessage(), e);
			}
		    }
		    updateStatusLabel();
		});

	    } catch (final Exception e1) {
		if (logger.isErrorEnabled()) {
		    logger.error(e1.getLocalizedMessage(), e1);
		}
	    } finally {
		setWorking(false);
	    }

	}).start();
    }

    private long countElements(final File file2) throws SAXException, IOException, ParserConfigurationException {
	final String tagName = "booking";
	final InputStream in = new FileInputStream(file);

	final SAXParserFactory spf = SAXParserFactory.newInstance();
	final SAXParser saxParser = spf.newSAXParser();
	final AtomicInteger counter = new AtomicInteger();
	saxParser.parse(in, new DefaultHandler() {
	    @Override
	    public void startElement(final String uri, final String localName, final String qName,
		    final Attributes attributes) {
		// System.err.println(uri);
		// System.err.println(localName);
		// System.err.println(qName);
		if (qName.equals(tagName)) {
		    counter.incrementAndGet();
		}
	    }

	});
	return counter.longValue();
    }

    public void saveState() {
	if (file == null || dataModel.getData().isEmpty()) {
	    return;
	}
	makeBackup();
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state to " + file);
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataStore.class);
	    final Marshaller jaxbMarshaller = jc.createMarshaller();
	    jaxbMarshaller.setListener(new MarshallListener());
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.marshal(dataModel.toDataStore(), file);
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state done");
	}
	final Preferences userPrefs = Preferences.userNodeForPackage(getClass());
	userPrefs.put(defaultDataFileNameKey, file.getAbsolutePath());

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

    private void showAddBookingDialog() {
	showAddBookingDialog(null);
    }

    private void showAddBookingDialog(final LocalDate date) {
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
	    c.setDataModel(dataModel);
	    c.setBookingManager(bookingManager);
	    c.datePickerCheckIn.setValue(date);
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
	final LocalDate now = LocalDate.now();
	final List<DateBean> dates = dataModel.getData();
	final Map<String, Multimap<LocalDate, String>> cleaningRoomMap = new LinkedHashMap<>();
	for (final DateBean db : dates) {
	    for (final RoomBean rb : db) {
		if (rb.hasCleaning()) {
		    Multimap<LocalDate, String> m = cleaningRoomMap.get(rb.getCleaning());
		    if (m == null) {
			m = LinkedHashMultimap.create();
			cleaningRoomMap.put(rb.getCleaning(), m);
		    }
		    m.put(db.getDate(), rb.getName());
		}
	    }
	}
	final Alert alert = new Alert(AlertType.INFORMATION);
	alert.setTitle("Cleaning Plan");
	final TextArea label = new TextArea();

	label.setEditable(false);
	label.setPrefHeight(400);
	label.setPrefWidth(400);
	label.getStyleClass().add("copyable-label");
	label.setText(getCleaningPlanString(cleaningRoomMap));
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
	    stage.setWidth(400);
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

    public void shutDown() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Shutting down");
	}

    }

    private void updateStatusLabel() {
	dataModel.getModelConfiguration().setNightCounting(NightCounting.DAY_BEFORE);
	Platform.runLater(() -> doUpdateStatusLabel());

    }

}

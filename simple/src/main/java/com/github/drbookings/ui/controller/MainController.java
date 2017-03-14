package com.github.drbookings.ui.controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.CellSelectionManager;
import com.github.drbookings.model.DataModel;
import com.github.drbookings.model.Dates;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.AuslastungCellFactory;
import com.github.drbookings.ui.AuslastungCellValueFactory;
import com.github.drbookings.ui.StudioCellFactory;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    private static final String defaultDataFileNameKey = "data";

    private static final String defaultDataFileName = "booking-data.xml";

    private static final DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, E");

    @FXML
    private Node node;

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
    private MenuItem menuItemExit;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    Button buttonAddBooking;

    private final ObservableSet<Integer> rowsWithSelectedCells = FXCollections.observableSet();

    private File file;

    private int currentMonth = LocalDate.now().getMonthValue();

    private final DataModel dataModel = new DataModel();

    private void addBooking() {
	final ObservableList<RoomBean> dates = CellSelectionManager.getInstance().getSelection();
	Platform.runLater(() -> showAddBookingDialog(dates.get(0).getDate()));
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
		if (((DateBean) cellData).getBookings().isEmpty()) {

		} else {
		    dataModel.removeAll(((DateBean) cellData).getBookings().get(0));
		}

	    }
	}
    }

    private void doUpdateStatusLabel() {

	final Pair<LocalDate, LocalDate> p = Dates.getFirstAndLastDayOfMonth(currentMonth);
	if (logger.isDebugEnabled()) {
	    logger.debug("First day: " + p.getLeft());
	    logger.debug("Last day: " + p.getRight());
	}
	final DecimalFormat nf = new DecimalFormat("#,###,###,##0.00");
	final int bookingDays = dataModel.getNumberOfBookingNights(p.getLeft(), p.getRight(), "(?i)booking");
	final double bookingEarnings = dataModel.getBruttoEarnings(p.getLeft(), p.getRight(), "(?i)booking");
	final int airbnbDays = dataModel.getNumberOfBookingNights(p.getLeft(), p.getRight(), "(?i)airbnb");
	final double airbnbEarnings = dataModel.getBruttoEarnings(p.getLeft(), p.getRight(), "(?i)airbnb");
	final int otherDays = dataModel.getNumberOfBookingNights(p.getLeft(), p.getRight(), "(?!airbnb|booking)");
	final double otherEarnings = dataModel.getBruttoEarnings(p.getLeft(), p.getRight(), "(?!airbnb|booking)");
	final String month = p.getLeft().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
	labelStatus.setText(month + ": Booking nights: " + bookingDays + " (" + nf.format(bookingEarnings)
		+ "€), Airbnb nights: " + airbnbDays + " (" + nf.format(airbnbEarnings) + "€), Other nights: "
		+ otherDays + " (" + nf.format(otherEarnings) + "€), Total: " + (airbnbDays + bookingDays + otherDays)
		+ " (" + nf.format(airbnbEarnings + bookingEarnings + otherEarnings) + "€)");
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
		    cells.add(((DateBean) cell).getRoom(c + ""));
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

	    final TableRow<DateBean> row = new TableRow<DateBean>() {
		@Override
		protected void updateItem(final DateBean item, final boolean empty) {
		    super.updateItem(item, empty);
		    if (empty || item == null) {
			setStyle("");
		    } else {
			if (item.getDate().isEqual(LocalDate.now())) {
			    setStyle("-fx-background-color: gold;");
			} else if (item.getDate()
				.equals(item.getDate().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()))) {
			    setStyle("-fx-background-color: lemonchiffon;");
			} else {
			    setStyle("");
			}
		    }
		}
	    };
	    final PseudoClass rowContainsSelectedCell = PseudoClass.getPseudoClass("contains-selection");
	    final BooleanBinding containsSelection = Bindings.createBooleanBinding(
		    () -> rowsWithSelectedCells.contains(row.getIndex()), rowsWithSelectedCells, row.indexProperty());
	    containsSelection.addListener((obs, didContainSelection, nowContainsSelection) -> row
		    .pseudoClassStateChanged(rowContainsSelectedCell, nowContainsSelection));
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
	    new Thread(() -> restoreState()).start();
	}
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

	tableView.getSelectionModel().getSelectedCells()
		.addListener((final ListChangeListener.Change<? extends TablePosition> c) -> {
		    rowsWithSelectedCells.clear();
		    final Set<Integer> rows = tableView.getSelectionModel().getSelectedCells().stream()
			    .map(pos -> pos.getRow()).collect(Collectors.toSet());
		    rowsWithSelectedCells.addAll(rows);
		});

	tableView.setItems(dataModel.getData());
	// tableView.getItems().addListener((ListChangeListener<DateBean>) c ->
	// {
	// final int cnt = 1;
	// while (c.next()) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Change " + cnt + " DateBean Added " + c.wasAdded());
	// logger.debug("Change " + cnt + " DateBean Removed " +
	// c.wasRemoved());
	// logger.debug("Change " + cnt + " DateBean Permut " +
	// c.wasPermutated());
	// logger.debug("Change " + cnt + " DateBean Replaced " +
	// c.wasReplaced());
	// logger.debug("Change " + cnt + " DateBean Updated " +
	// c.wasUpdated());
	// logger.debug("");
	// }
	//
	// }
	// });
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
	cStudio1.setCellFactory(new StudioCellFactory("1"));

	cStudio2.setCellFactory(new StudioCellFactory("2"));

	cStudio3.setCellFactory(new StudioCellFactory("3"));

	cStudio4.setCellFactory(new StudioCellFactory("4"));

	cAuslastung.setCellFactory(new AuslastungCellFactory());
	cAuslastung.setCellValueFactory(new AuslastungCellValueFactory());

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
	tableView.getSelectionModel().setCellSelectionEnabled(true);
	tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	tableView.setContextMenu(menu);
	tableView.addEventHandler(MouseEvent.MOUSE_CLICKED, t -> {
	    if (t.getButton() == MouseButton.SECONDARY) {
		menu.show(tableView, t.getScreenX(), t.getScreenY());
	    }
	});

	tableView.getSelectionModel().getSelectedCells().addListener(getCellSelectionListener());
	tableView.setOnMousePressed(event -> {
	    if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		handleTableSelectEvent(event);
	    }
	});
	tableView.setRowFactory(getRowFactory());
	initStatusLabel();
	initDataFile();
    }

    private void initStatusLabel() {
	CellSelectionManager.getInstance().getSelection().addListener((ListChangeListener<RoomBean>) c -> {
	    if (c.getList().isEmpty()) {
		return;
	    }
	    final int currentMonth = c.getList().get(0).getDate().getMonthValue();
	    if (this.currentMonth != currentMonth) {
		if (logger.isDebugEnabled()) {
		    logger.debug("Current month: " + currentMonth);
		}
		this.currentMonth = currentMonth;
		updateStatusLabel();
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
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataModel.class);
	    final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();
	    dataModel.setData(((DataModel) jaxbMarshaller.unmarshal(file)).getData());
	    updateStatusLabel();
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	}
    }

    private void saveState() {
	if (file == null || dataModel.getData().isEmpty()) {
	    return;
	}
	makeBackup();
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state to " + file);
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataModel.class);
	    final Marshaller jaxbMarshaller = jc.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.marshal(dataModel, file);
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state done");
	}
	final Preferences userPrefs = Preferences.userNodeForPackage(getClass());
	userPrefs.put(defaultDataFileNameKey, file.getAbsolutePath());

    }

    private void showAddBookingDialog() {
	showAddBookingDialog(null);
    }

    private void showAddBookingDialog(final LocalDate date) {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddBookingView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root, 400, 400);
	    stage.setTitle("Add Booking");
	    stage.setScene(scene);
	    final AddBookingController c = loader.getController();
	    c.setDataModel(dataModel);
	    c.datePickerCheckIn.setValue(date);
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    private void showCleaningPlan() {
	final LocalDate now = LocalDate.now();
	final List<DateBean> dates = dataModel.getAfter(LocalDate.of(now.getYear(), currentMonth, 1));
	// final Multimap<String, LocalDate> cleaningMap =
	// ArrayListMultimap.create();
	final Map<String, Multimap<LocalDate, String>> cleaningRoomMap = new LinkedHashMap<>();
	for (final DateBean db : dates) {
	    for (final RoomBean rb : db) {
		if (rb.hasCleaning()) {
		    // cleaningMap.put(rb.getCleaning(), db.getDate());
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
	final String month = LocalDate.of(now.getYear(), currentMonth, now.minusDays(1).getDayOfMonth()).getMonth()
		.getDisplayName(TextStyle.FULL, Locale.getDefault());
	alert.setHeaderText("Cleaning Plan from " + month);
	alert.setContentText(null);
	alert.getDialogPane().setContent(label);
	alert.showAndWait();
    }

    private void showRoomDetailsDialog() {
	try {
	    final FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RoomDetailsView.fxml"));
	    final Parent root = loader.load();
	    final Stage stage = new Stage();
	    final Scene scene = new Scene(root, 400, 400);
	    stage.setTitle("Room Details");
	    stage.setScene(scene);
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
	final Alert alert = new Alert(AlertType.CONFIRMATION);
	alert.setTitle("Save changes?");
	alert.setHeaderText("Save changes?");

	final Optional<ButtonType> result = alert.showAndWait();
	if (result.get() == ButtonType.OK) {
	    new Thread(() -> saveState()).start();
	} else {

	}
    }

    private void updateStatusLabel() {
	Platform.runLater(() -> doUpdateStatusLabel());

    }

}

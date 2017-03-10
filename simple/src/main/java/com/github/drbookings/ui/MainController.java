package com.github.drbookings.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.RoomSelectionManager;
import com.github.drbookings.model.DataModel;
import com.github.drbookings.model.Dates;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.css.PseudoClass;
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
    private MenuItem menuItemExit;

    @FXML
    private MenuItem menuItemOpen;

    @FXML
    Button buttonAddBooking;

    private final ObservableSet<Integer> rowsWithSelectedCells = FXCollections.observableSet();

    private File file;

    private int currentMonth = -1;

    private void addBooking() {
	final ObservableList<RoomBean> rooms = RoomSelectionManager.getInstance().getSelection();
	final DateBean db = rooms.get(0).getDateBean();
	Platform.runLater(() -> showAddBookingDialog(db.getDate()));
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
		final DateBean bean = (DateBean) cellData;
		final RoomBean room = bean.getRoom(Integer.valueOf(c).toString());
		DataModel.getInstance().removeAll(room.getBookings());
	    } else {
		if (logger.isDebugEnabled()) {
		    logger.debug("Delete row " + r);
		}
	    }
	}
    }

    private ListChangeListener<TablePosition> getCellSelectionListener() {
	return c -> {
	    final List<RoomBean> roomBeans = new ArrayList<>();
	    for (final TablePosition tp : c.getList()) {
		final int c2 = tp.getColumn();
		final int r = tp.getRow();
		final Object cell = tp.getTableColumn().getCellData(r);
		if (cell instanceof DateBean) {
		    final DateBean db = (DateBean) cell;
		    final RoomBean rb = db.getRoom(Integer.valueOf(c2).toString());
		    roomBeans.add(rb);
		}
	    }
	    RoomSelectionManager.getInstance().setSelection(roomBeans);

	};
    }

    private String getCleaningPlanString(final Multimap<String, LocalDate> cleaningMap) {
	final StringBuilder sb = new StringBuilder();
	for (final Entry<String, Collection<LocalDate>> e : cleaningMap.asMap().entrySet()) {
	    sb.append(e.getKey());
	    sb.append("\t");
	    for (final Iterator<LocalDate> it = e.getValue().iterator(); it.hasNext();) {
		final LocalDate v = it.next();
		sb.append(v.format(myDateFormatter));
		sb.append("\n");
		if (it.hasNext()) {
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
			    setStyle("-fx-background-color: lightgreen;");
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
	fileChooser.setInitialDirectory(file.getParentFile());
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

	tableView.setItems(DataModel.getInstance().getData());
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
	cStudio1.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
	cStudio2.setCellFactory(new StudioCellFactory("2"));
	cStudio2.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
	cStudio3.setCellFactory(new StudioCellFactory("3"));
	cStudio3.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));
	cStudio4.setCellFactory(new StudioCellFactory("4"));
	cStudio4.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue()));

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
	RoomSelectionManager.getInstance().getSelection().addListener((ListChangeListener<RoomBean>) c -> {
	    if (c.getList().isEmpty()) {
		return;
	    }
	    final int currentMonth = c.getList().get(0).getDateBean().getDate().getMonthValue();
	    if (this.currentMonth != currentMonth) {
		if (logger.isDebugEnabled()) {
		    logger.debug("Current month: " + currentMonth);
		}
		this.currentMonth = currentMonth;
		updateStatusLabel();
	    }
	});

    }

    private void restoreState() {
	// final File file = new File(System.getProperty("user.home"),
	// "booking-data.xml");
	if (logger.isInfoEnabled()) {
	    logger.info("Loading state from " + file.getAbsolutePath());
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataModel.class);
	    final Unmarshaller jaxbMarshaller = jc.createUnmarshaller();
	    DataModel.getInstance().setAll(((DataModel) jaxbMarshaller.unmarshal(file)).getData());
	} catch (final Exception e1) {
	    logger.error(e1.getLocalizedMessage(), e1);
	}
    }

    private void saveState() {
	if (file == null) {
	    return;
	}
	if (logger.isDebugEnabled()) {
	    logger.debug("Saving state to " + file);
	}
	try {
	    final JAXBContext jc = JAXBContext.newInstance(DataModel.class);
	    final Marshaller jaxbMarshaller = jc.createMarshaller();
	    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	    jaxbMarshaller.marshal(DataModel.getInstance(), file);
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
	    c.datePickerCheckIn.setValue(date);
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    private void showCleaningPlan() {
	final LocalDate now = LocalDate.now();
	final List<DateBean> dates = DataModel.getInstance()
		.getAfter(LocalDate.of(now.getYear(), currentMonth, now.getDayOfMonth()));
	final Multimap<String, LocalDate> cleaningMap = ArrayListMultimap.create();
	for (final DateBean db : dates) {
	    for (final RoomBean rb : db) {
		if (rb.hasCleaning()) {
		    cleaningMap.put(rb.getCleaning(), db.getDate());
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
	label.setText(getCleaningPlanString(cleaningMap));
	final String month = LocalDate.of(now.getYear(), currentMonth, now.getDayOfMonth()).getMonth()
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
	    stage.setOnCloseRequest(event -> Platform.runLater(() -> c.update()));
	    stage.show();
	} catch (final IOException e) {
	    logger.error(e.getLocalizedMessage(), e);
	}
    }

    public void shutDown() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Shutting down");
	}
	new Thread(() -> saveState()).start();
    }

    private void updateStatusLabel() {
	final Pair<LocalDate, LocalDate> p = Dates.getFirstAndLastDayOfMonth(currentMonth);
	if (logger.isDebugEnabled()) {
	    logger.debug("First day: " + p.getLeft());
	    logger.debug("Last day: " + p.getRight());
	}
	final int bookingDays = DataModel.getInstance().getNumberOfBookingDays(p.getLeft(), p.getRight(), "booking");
	final int airbnbDays = DataModel.getInstance().getNumberOfBookingDays(p.getLeft(), p.getRight(), "airbnb");
	final int otherDays = DataModel.getInstance().getNumberOfBookingDays(p.getLeft(), p.getRight(), "");

	final String month = p.getLeft().getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());

	labelStatus.setText(month + ": Booking days: " + bookingDays + ", Airbnb days: " + airbnbDays + ", Other days: "
		+ otherDays);
	labelStatus.setStyle("-fx-font-size: 14pt;");
    }

}

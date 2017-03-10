package com.github.drbookings;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.DataModel;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;

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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;

public class MainController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private TableView<DateBean> tableView;

    @FXML
    private TableColumn<DateBean, LocalDate> cDate;

    @FXML
    private TableColumn<DateBean, DateBean> cStudio1;

    @FXML
    private TableColumn<DateBean, DateBean> cStudio2;

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
			    setStyle("-fx-background-color:lightgreen");
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
    private void handleMenuItemExit(final ActionEvent event) {
	if (logger.isDebugEnabled()) {
	    logger.debug("MenuItem Exit");
	}
    }

    @FXML
    private void handleMenuItemOpen(final ActionEvent event) {
	if (logger.isDebugEnabled()) {
	    logger.debug("MenuItem Open");
	}
    }

    private void handleTableSelectEvent(final MouseEvent event) {
	Platform.runLater(() -> showRoomDetailsDialog());
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
	final DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, E");
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
	menu.getItems().addAll(mi1, mi2);
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

}

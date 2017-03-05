package com.github.drbookings;

import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.api.BookingDay;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public class FXMLController implements Initializable {

	private TableColumn<BookingDay, LocalDate> c1;
	private TableColumn<BookingDay, TableView<Booking>> c2;

	final ObservableList<Integer> highlightRows = FXCollections.observableArrayList();

	@FXML
	private TableView<BookingDay> table;

	private TableView<Booking> buildTable(final ObservableList<Booking> bookings) {
		final TableColumn<Booking, String> c1 = new TableColumn<>("ID");
		final TableColumn<Booking, String> c2 = new TableColumn<>("Status");
		final TableColumn<Booking, List<String>> c3 = new TableColumn<>("Names");

		c1.setCellValueFactory(cellData -> cellData.getValue().getId());
		c2.setCellValueFactory(cellData -> cellData.getValue().getStatus());
		c3.setCellValueFactory(cellData -> cellData.getValue().getGuestNamesValue());

		final TableView<Booking> result = new TableView<>(bookings);

		result.getColumns().addAll(c1, c2, c3);

		result.setFixedCellSize(25);
		result.prefHeightProperty().bind(Bindings.size(result.getItems()).multiply(result.getFixedCellSize()).add(30));

		return result;
	}

	private void createTable() {
		c1 = new TableColumn<>("Date");
		c1.setCellValueFactory(cellData -> cellData.getValue().getDate());
		c2 = new TableColumn<>("Booking");
		c2.setCellValueFactory(cellData -> {

			return new SimpleObjectProperty<TableView<Booking>>(buildTable(cellData.getValue().getBookings()));

		});

		// Table cell coloring
		c1.setCellFactory(param -> new TableCell<BookingDay, LocalDate>() {

			@Override
			public void updateItem(final LocalDate item, final boolean empty) {
				super.updateItem(item, empty);
				if (!isEmpty()) {
					if (item.getDayOfYear() == LocalDate.now().getDayOfYear()) {
						highlightRows.setAll(getTableRow().getIndex());
					}
					setText(item.toString());
				}
			}

		});

		table.setRowFactory(tableView -> {
			final TableRow<BookingDay> row = new TableRow<BookingDay>() {
				@Override
				protected void updateItem(final BookingDay person, final boolean empty) {
					super.updateItem(person, empty);
					if (highlightRows.contains(getIndex())) {
						if (!getStyleClass().contains("highlightedRow")) {
							getStyleClass().add("highlightedRow");
						}
					} else {
						getStyleClass().removeAll(Collections.singleton("highlightedRow"));
					}
				}
			};
			highlightRows.addListener((ListChangeListener<Integer>) change -> {
				if (highlightRows.contains(row.getIndex())) {
					if (!row.getStyleClass().contains("highlightedRow")) {
						row.getStyleClass().add("highlightedRow");
					}
				} else {
					row.getStyleClass().removeAll(Collections.singleton("highlightedRow"));
				}
			});
			return row;
		});

		table.getColumns().addAll(c1, c2);

	}

	public TableView<BookingDay> getTable() {
		return table;
	}

	@FXML
	void handleButtonDelete(final ActionEvent event) {
		System.out.println(event);
	}

	@FXML
	void handleButtonModify(final ActionEvent event) {
		System.out.println(event);
	}

	@FXML
	void handleButtonNew(final ActionEvent event) {
		System.out.println(event);
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {

		createTable();
	}

}
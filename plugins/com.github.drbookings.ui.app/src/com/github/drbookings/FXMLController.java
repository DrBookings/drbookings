package com.github.drbookings;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

import com.github.drbookings.core.datamodel.api.Booking;
import com.github.drbookings.core.datamodel.api.BookingDay;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class FXMLController implements Initializable {

	private TableColumn<BookingDay, LocalDate> c1;
	private TableColumn<BookingDay, List<Booking>> c2;

	@FXML
	private TableView<BookingDay> table;

	private void createTable() {
		c1 = new TableColumn<>("Date");
		c1.setCellValueFactory(cellData -> cellData.getValue().getDate());
		c2 = new TableColumn<>("Booking ID");
		c2.setCellValueFactory(cellData -> cellData.getValue().getBookingsValue());
		// c3 = new TableColumn<>("Check-Out");
		// c3.setCellValueFactory(cellData ->
		// cellData.getValue().getCheckOut());
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
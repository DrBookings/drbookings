package com.github.drbookings;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.github.drbookings.core.datamodel.api.Booking;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

public class FXMLController implements Initializable {

	private TableColumn<Booking, String> c1;
	private TableColumn<Booking, LocalDate> c2;
	private TableColumn<Booking, LocalDate> c3;

	@FXML
	private AnchorPane pane;

	@FXML
	private TableView<Booking> table;

	private void createTable() {
		c1 = new TableColumn<>("Booking ID");
		c1.setCellValueFactory(cellData -> cellData.getValue().getId());
		c2 = new TableColumn<>("Check-In");
		c2.setCellValueFactory(cellData -> cellData.getValue().getCheckIn());
		c3 = new TableColumn<>("Check-Out");
		c3.setCellValueFactory(cellData -> cellData.getValue().getCheckOut());
		table.getColumns().addAll(c1, c2, c3);

	}

	public TableView<Booking> getTable() {
		return table;
	}

	@Override
	public void initialize(final URL url, final ResourceBundle rb) {

		createTable();
	}

}
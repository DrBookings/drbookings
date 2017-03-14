package com.github.drbookings.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.RoomBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CellContentController implements Initializable {

    private static Node buildEntryCheckIn(final String guestName) {
	final VBox result = new VBox();
	result.setStyle("-fx-background-color: darkorange;");
	final Label l = new Label("Check-in " + guestName);
	l.setMaxWidth(Double.POSITIVE_INFINITY);
	l.setAlignment(Pos.CENTER);
	result.getChildren().add(l);
	return result;
    }

    private static Node buildEntryCheckOut(final String guestName) {
	final VBox result = new VBox();
	final Label l = new Label("Check-out " + guestName);
	l.setMaxWidth(Double.POSITIVE_INFINITY);
	l.setAlignment(Pos.CENTER);
	result.getChildren().add(l);
	return result;
    }

    private static Node buildEntryStay(final String guestName) {
	final VBox result = new VBox();
	final Label l = new Label(guestName);
	l.setMaxWidth(Double.POSITIVE_INFINITY);
	l.setAlignment(Pos.CENTER);
	result.getChildren().add(l);
	return result;
    }

    @FXML
    private VBox guestNamesCheckIn;

    @FXML
    private VBox guestNamesCheckOut;

    @FXML
    private VBox guestNamesStay;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public boolean setData(final RoomBean roomBean) {
	boolean changed = false;
	guestNamesCheckIn.getChildren().clear();
	guestNamesCheckOut.getChildren().clear();
	guestNamesStay.getChildren().clear();
	for (final BookingBean bb : roomBean.getBookings()) {
	    if (bb.isCheckOut()) {
		guestNamesCheckIn.getChildren().add(buildEntryCheckOut(bb.getGuestName()));
	    } else if (bb.isCheckIn()) {
		guestNamesCheckOut.getChildren().add(buildEntryCheckIn(bb.getGuestName()));
	    } else {
		guestNamesStay.getChildren().add(buildEntryStay(bb.getGuestName()));
	    }
	    changed = true;
	}
	return changed;
    }

}

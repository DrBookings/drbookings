package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.Styles;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CellContentController implements Initializable {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(CellContentController.class);

    private static Node buildEntryCheckIn(final BookingEntry e) {
	final Label l = getNewLabel(e.getElement().getGuest().getName());
	l.getStyleClass().add("check-in");
	return l;
    }

    private static Node buildEntryCheckOut(final BookingEntry e) {
	final Label l = getNewLabel(e.getElement().getGuest().getName());
	l.getStyleClass().add("check-in");
	return l;
    }

    private static Node buildEntryCleaning(final RoomBean rb) {
	final Label l = getNewLabel("");
	final String s;
	if (rb.needsCleaning()) {
	    s = "No Cleaning";
	    if (rb.getDate().isAfter(LocalDate.now().minusDays(7))) {
		l.getStyleClass().add("cleaning-warning");
	    }
	} else {
	    s = "Cleaning " + rb.getCleaningEntry().getElement().getName();
	    l.getStyleClass().add("cleaning");
	}
	l.setText(s);
	return l;
    }

    private static Node buildEntryStay(final BookingEntry booking) {
	final Label l = getNewLabel(booking.getElement().getGuest().getName());
	if (!LocalDate.now().equals(booking.getDate()) && !booking.getDate()
		.equals(booking.getDate().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()))) {
	    l.getStyleClass().add("entry-stay");
	}
	return l;

    }

    private static Label getNewLabel(final String text) {
	final Label l = new Label(text);
	l.setMaxWidth(Double.POSITIVE_INFINITY);
	l.setAlignment(Pos.CENTER);
	l.setPadding(new Insets(2));
	return l;
    }

    @FXML
    private VBox guestNamesCheckIn;

    @FXML
    private VBox guestNamesCheckOut;

    @FXML
    private VBox guestNamesStay;

    @FXML
    private VBox cleaning;

    @FXML
    private Parent cellContainer;

    public Parent getCellContainer() {
	return cellContainer;
    }

    public VBox getCleaning() {
	return cleaning;
    }

    public VBox getGuestNamesCheckIn() {
	return guestNamesCheckIn;
    }

    public VBox getGuestNamesCheckOut() {
	return guestNamesCheckOut;
    }

    public VBox getGuestNamesStay() {
	return guestNamesStay;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public void setCellContainer(final Parent cellContainer) {
	this.cellContainer = cellContainer;
    }

    public void setCleaning(final VBox cleaning) {
	this.cleaning = cleaning;
    }

    public void setData(final RoomBean rb) {
	if (rb == null) {
	    return;
	}
	if (rb.hasCleaning() || rb.needsCleaning()) {
	    cleaning.getChildren().add(buildEntryCleaning(rb));
	} else {
	    cleaning.getChildren().clear();
	}
	guestNamesCheckOut.getChildren().clear();
	guestNamesStay.getChildren().clear();
	guestNamesCheckIn.getChildren().clear();

	BookingEntry last = null;
	for (final BookingEntry bb : rb.getFilteredBookingEntries().stream()
		.sorted(Comparator.comparing(BookingEntry::isCheckOut)).collect(Collectors.toList())) {
	    if (bb.isCheckIn()) {
		guestNamesCheckIn.getChildren().add(buildEntryCheckIn(bb));
	    } else if (bb.isCheckOut()) {
		guestNamesCheckOut.getChildren().add(buildEntryCheckOut(bb));
	    } else {
		guestNamesStay.getChildren().add(buildEntryStay(bb));
	    }
	    last = bb;
	}

	if (rb.isWarning()) {
	    if (rb.hasCheckIn()) {
		cellContainer.getStyleClass().add("warning-box-top");
	    } else if (rb.hasCheckOut()) {
		cellContainer.getStyleClass().add("warning-box-bottom");
	    } else {
		cellContainer.getStyleClass().add("warning-box-middle");
	    }
	}
	if (last != null) {
	    cellContainer.getStyleClass()
		    .add(Styles.getBackgroundStyleSource(last.getElement().getBookingOrigin().getName()));
	}
    }

    public void setGuestNamesCheckIn(final VBox guestNamesCheckIn) {
	this.guestNamesCheckIn = guestNamesCheckIn;
    }

    public void setGuestNamesCheckOut(final VBox guestNamesCheckOut) {
	this.guestNamesCheckOut = guestNamesCheckOut;
    }

    public void setGuestNamesStay(final VBox guestNamesStay) {
	this.guestNamesStay = guestNamesStay;
    }

}

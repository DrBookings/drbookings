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
	l.getStyleClass().add("check-out");
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
    private VBox guestNames0;

    @FXML
    private VBox guestNames1;

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

    public VBox getGuestNames0() {
	return guestNames0;
    }

    public VBox getGuestNames1() {
	return guestNames1;
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
	    cleaning.getChildren().add(getNewLabel(null));
	}
	guestNames1.getChildren().clear();
	guestNames0.getChildren().clear();

	BookingEntry last = null;
	for (final BookingEntry bb : rb.getFilteredBookingEntries().stream()
		.sorted(Comparator.comparing(BookingEntry::isCheckOut)).collect(Collectors.toList())) {
	    if (bb.isCheckIn()) {
		guestNames0.getChildren().add(buildEntryCheckIn(bb));
	    } else if (bb.isCheckOut()) {
		guestNames1.getChildren().add(buildEntryCheckOut(bb));
	    } else {
		guestNames0.getChildren().add(buildEntryStay(bb));
	    }
	    last = bb;
	}

	if (guestNames0.getChildren().isEmpty()) {
	    guestNames0.getChildren().add(getNewLabel(null));
	}

	if (guestNames1.getChildren().isEmpty()) {
	    guestNames1.getChildren().add(getNewLabel(null));
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

    public void setGuestNames0(final VBox guestNamesCheckIn) {
	this.guestNames0 = guestNamesCheckIn;
    }

    public void setGuestNames1(final VBox guestNamesCheckOut) {
	this.guestNames1 = guestNamesCheckOut;
    }

}

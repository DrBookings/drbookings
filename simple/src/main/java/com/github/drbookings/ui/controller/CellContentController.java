package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.BookingBeans;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.Styles;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CellContentController implements Initializable {

    private static Logger logger = LoggerFactory.getLogger(CellContentController.class);

    private static Node buildEntryCheckIn(final BookingBean bookingBean) {

	final Label l = getNewLabel("Check-in\n" + bookingBean.getGuestName());
	if (bookingBean.getRoom().getDateBean().getDataModel().getConnectedPrevious(bookingBean.getRoom())
		.isPresent()) {
	    final RoomBean rb = bookingBean.getRoom().getDateBean().getDataModel()
		    .getConnectedPrevious(bookingBean.getRoom()).get();
	    if (BookingBeans.guestNameView(rb.getAllBookings()).contains(bookingBean.getGuestName())) {

		// ignore same guest check-in

		return l;
	    }
	}
	l.getStyleClass().add("check-in");

	return l;
    }

    private static Node buildEntryCheckOut(final BookingBean booking) {
	final Label l = getNewLabel(booking.getGuestName() + "\n" + "Check-out");
	return l;
    }

    private static Node buildEntryCleaning(final RoomBean rb) {
	final Label l = getNewLabel("");
	String s = "Cleaning " + rb.getCleaning();
	if (rb.isNeedsCleaning()) {
	    s = "No Cleaning";
	    if (rb.getDate().isAfter(LocalDate.now().minusDays(7))) {
		l.getStyleClass().add("cleaning-warning");
	    }
	} else {
	    l.getStyleClass().add("cleaning");
	}

	l.setText(s);
	return l;
    }

    private static Node buildEntryStay(final BookingBean booking) {
	final Label l = getNewLabel(booking.getGuestName());

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

    public void setData(final RoomBean roomBean) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Updating cell content for " + roomBean);
	// }
	if (roomBean.hasCleaning() || roomBean.isNeedsCleaning()) {
	    cleaning.getChildren().add(buildEntryCleaning(roomBean));
	} else {
	    cleaning.getChildren().clear();
	}
	guestNamesCheckOut.getChildren().clear();
	guestNamesStay.getChildren().clear();
	guestNamesCheckIn.getChildren().clear();

	BookingBean last = null;
	for (final BookingBean bb : roomBean.getFilteredBookings()) {
	    if (bb.isCheckOut()) {
		guestNamesCheckOut.getChildren().add(buildEntryCheckOut(bb));
	    } else if (bb.isCheckIn()) {
		guestNamesCheckIn.getChildren().add(buildEntryCheckIn(bb));
	    } else {
		guestNamesStay.getChildren().add(buildEntryStay(bb));
	    }
	    last = bb;
	}

	if (roomBean.isWarning()) {
	    if (roomBean.hasCheckIn()) {
		cellContainer.getStyleClass().add("warning-box-top");
	    } else if (roomBean.hasCheckOut()) {
		cellContainer.getStyleClass().add("warning-box-bottom");
	    } else {
		cellContainer.getStyleClass().add("warning-box-middle");
	    }
	} else if (last != null) {
	    cellContainer.getStyleClass().add(Styles.getBackgroundStyleSource(last.getSource()));
	}
	// cellContainer.requestLayout();
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

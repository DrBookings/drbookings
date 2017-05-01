package com.github.drbookings.ui.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.drbookings.LocalDates;
import com.github.drbookings.model.data.Booking;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class BookingDetailsController implements Initializable {

    private static void addDates(final HBox content, final BookingEntry be) {
	final TextFlow checkIn = LocalDates.getDateText(be.getElement().getCheckIn());
	final TextFlow checkOut = LocalDates.getDateText(be.getElement().getCheckOut());
	final TextFlow year = LocalDates.getYearText(be.getElement().getCheckOut());
	final TextFlow tf = new TextFlow();
	// tf.getChildren().addAll(checkIn, new Text(" ➤ "), checkOut, new
	// Text("\n"), year);
	tf.getChildren().addAll(checkIn, new Text(" ➤ "), checkOut);
	// HBox.setHgrow(tf, Priority.SOMETIMES);
	content.getChildren().add(tf);

    }

    private static void addName(final HBox content, final BookingEntry be) {
	final Label label = new Label(be.getElement().getGuest().getName());
	label.setWrapText(true);
	content.getChildren().add(label);
	// HBox.setHgrow(label, Priority.SOMETIMES);
    }

    private static void addNights(final HBox content, final BookingEntry be) {
	final Text label = new Text(be.getElement().getNumberOfNights() + " nights");
	content.getChildren().add(label);
	// HBox.setHgrow(label, Priority.SOMETIMES);
    }

    private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> update(c.getList()));

    @FXML
    private VBox content;

    private final Map<Booking, TextInputControl> booking2CheckInNote = new HashMap<>();

    private final Map<Booking, TextInputControl> booking2CheckOutNote = new HashMap<>();

    private final Map<Booking, TextInputControl> booking2SpecialRequestNote = new HashMap<>();

    private final Map<Booking, TextInputControl> booking2GrossEarnings = new HashMap<>();

    private final Map<Booking, CheckBox> booking2WelcomeMail = new HashMap<>();

    private final Map<Booking, CheckBox> booking2Payment = new HashMap<>();

    private void addCheckInNote(final Pane content, final BookingEntry be) {
	final VBox b = new VBox();
	b.getChildren().add(new Text("Check-in Note"));
	final TextArea ta0 = new TextArea(be.getElement().getCheckInNote());
	ta0.setWrapText(true);
	ta0.setPrefHeight(80);
	b.getChildren().add(ta0);
	booking2CheckInNote.put(be.getElement(), ta0);
	content.getChildren().add(b);

    }

    private void addCheckOutNote(final Pane content, final BookingEntry be) {
	final VBox b = new VBox();
	b.getChildren().add(new Text("Check-out Note"));
	final TextArea ta0 = new TextArea(be.getElement().getCheckOutNote());
	ta0.setWrapText(true);
	ta0.setPrefHeight(80);
	b.getChildren().add(ta0);
	booking2CheckOutNote.put(be.getElement(), ta0);
	content.getChildren().add(b);

    }

    private void addRow0(final BookingEntry be) {
	final HBox box = new HBox(20);
	box.setFillHeight(true);
	addName(box, be);
	addDates(box, be);
	addNights(box, be);
	box.getStyleClass().add("first-line");
	content.getChildren().add(box);

    }

    private void addRow1(final BookingEntry be) {
	final HBox box = new HBox();
	box.setFillHeight(true);
	addCheckInNote(box, be);
	addCheckOutNote(box, be);
	content.getChildren().add(box);

    }

    private void addRow2(final BookingEntry be) {
	final HBox box = new HBox();
	box.setFillHeight(true);
	addSpecialRequestNote(box, be);
	content.getChildren().add(box);

    }

    private void addRow3(final BookingEntry be) {
	final HBox box = new HBox(4);
	box.setAlignment(Pos.CENTER_LEFT);
	box.setFillHeight(true);
	final TextField grossEarningsExpression = new TextField(be.getElement().getGrossEarningsExpression());
	grossEarningsExpression.setPrefWidth(120);
	booking2GrossEarnings.put(be.getElement(), grossEarningsExpression);
	final Text grossEarnings = new Text(String.format("%3.2f", be.getElement().getGrossEarnings()));
	final TextFlow tf = new TextFlow(new Text("Gross Earnings: "), grossEarningsExpression, new Text(" = "),
		grossEarnings, new Label("€"));
	box.getChildren().addAll(tf);
	content.getChildren().add(box);

    }

    private void addRow4(final BookingEntry be) {
	final HBox box = new HBox(8);
	box.setAlignment(Pos.CENTER_LEFT);
	box.setFillHeight(true);
	final TextFlow tf = new TextFlow();
	final Text t0 = new Text("Net Earnings: \t");
	final Text netEarnings = new Text(String.format("%3.2f", be.getElement().getNetEarnings()));
	final Text t1 = new Text("€ total \t");
	final Text netEarningsDay = new Text(String.format("%3.2f", be.getNetEarnings()));
	final Text t2 = new Text("€ /day");
	tf.getChildren().addAll(t0, netEarnings, t1, netEarningsDay, t2);
	box.getChildren().addAll(tf);
	content.getChildren().add(box);

    }

    private void addRow5(final BookingEntry be) {
	final HBox box = new HBox();
	box.setFillHeight(true);
	final Text t0 = new Text("Welcome Mail sent: ");
	final CheckBox cb0 = new CheckBox();
	cb0.setSelected(be.getElement().isWelcomeMailSend());
	booking2WelcomeMail.put(be.getElement(), cb0);
	final Text t1 = new Text(" \tPayment done: ");
	final CheckBox cb1 = new CheckBox();
	cb1.setSelected(be.getElement().isPaymentDone());
	booking2Payment.put(be.getElement(), cb1);
	final TextFlow tf = new TextFlow();
	tf.getChildren().addAll(t0, cb0, t1, cb1);
	box.getChildren().add(tf);
	content.getChildren().add(box);

    }

    private void addSeparator() {
	final HBox bb = new HBox();
	bb.setPrefHeight(20);
	bb.setAlignment(Pos.CENTER);
	final Separator s = new Separator();
	s.getStyleClass().add("large-separator");
	bb.getChildren().add(s);
	HBox.setHgrow(s, Priority.ALWAYS);
	content.getChildren().add(bb);
    }

    private void addSpecialRequestNote(final Pane content, final BookingEntry be) {
	final VBox b = new VBox();
	b.getChildren().add(new Text("Special Requests"));
	final TextArea ta0 = new TextArea(be.getElement().getSpecialRequestNote());
	ta0.setWrapText(true);
	ta0.setPrefHeight(80);
	b.getChildren().add(ta0);
	booking2SpecialRequestNote.put(be.getElement(), ta0);
	content.getChildren().add(b);

    }

    private void clearAll() {
	booking2CheckInNote.clear();
	booking2CheckOutNote.clear();
	booking2SpecialRequestNote.clear();
	booking2GrossEarnings.clear();
	content.getChildren().clear();
    }

    @FXML
    public void handleActionSaveBookingDetails(final ActionEvent e) {
	for (final Entry<Booking, TextInputControl> en : booking2CheckInNote.entrySet()) {
	    en.getKey().setCheckInNote(en.getValue().getText());
	}
	for (final Entry<Booking, TextInputControl> en : booking2CheckOutNote.entrySet()) {
	    en.getKey().setCheckOutNote(en.getValue().getText());
	}
	for (final Entry<Booking, TextInputControl> en : booking2SpecialRequestNote.entrySet()) {
	    en.getKey().setSpecialRequestNote(en.getValue().getText());
	}
	for (final Entry<Booking, TextInputControl> en : booking2GrossEarnings.entrySet()) {
	    en.getKey().setGrossEarningsExpression(en.getValue().getText());
	}
	for (final Entry<Booking, CheckBox> en : booking2Payment.entrySet()) {
	    en.getKey().setPaymentDone(en.getValue().isSelected());
	}
	for (final Entry<Booking, CheckBox> en : booking2WelcomeMail.entrySet()) {
	    en.getKey().setWelcomeMailSend(en.getValue().isSelected());
	}
	final Stage stage = (Stage) content.getScene().getWindow();
	stage.close();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	CellSelectionManager.getInstance().getSelection().addListener(roomListener);
	update(CellSelectionManager.getInstance().getSelection());

    }

    private void update(final ObservableList<? extends RoomBean> rooms) {
	clearAll();
	final Set<BookingEntry> bookings = rooms.stream().flatMap(r -> r.getBookingEntries().stream())
		.collect(Collectors.toSet());
	for (final Iterator<BookingEntry> it = bookings.iterator(); it.hasNext();) {
	    final BookingEntry be = it.next();
	    addRow0(be);
	    content.getChildren().add(new Separator());
	    addRow3(be);
	    content.getChildren().add(new Separator());
	    addRow4(be);
	    content.getChildren().add(new Separator());
	    addRow5(be);
	    content.getChildren().add(new Separator());
	    addRow1(be);
	    addRow2(be);

	    if (it.hasNext()) {
		addSeparator();
	    }
	}
    }

}

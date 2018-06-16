/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2018 Alexander Kerner
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 */

package com.github.drbookings.ui.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.LocalDates;
import com.github.drbookings.model.Payment;
import com.github.drbookings.model.data.BookingBean;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.Styles;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.dialogs.ModifyBookingDialogFactory;
import com.github.drbookings.ui.selection.RoomBeanSelectionManager;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.converter.NumberStringConverter;

public class BookingDetailsController implements Initializable {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private static final int prefTextInputFieldWidth = 80;
    private static final int boxSpacing = 4;
    private static final Insets boxPadding = new Insets(boxSpacing);
    private static final Logger logger = LoggerFactory.getLogger(BookingDetailsController.class);

    private static void addDates(final HBox content, final BookingBean be) {
	final TextFlow checkIn = LocalDates.getDateText(be.getCheckIn());
	final TextFlow checkOut = LocalDates.getDateText(be.getCheckOut());
	final TextFlow year = LocalDates.getYearText(be.getCheckOut());
	final TextFlow tf = new TextFlow();
	tf.getChildren().addAll(checkIn, new Text("\n"), checkOut, new Text("\n"), year);
	// tf.getChildren().addAll(checkIn, new Text(" ➤ "), checkOut);
	// HBox.setHgrow(tf, Priority.SOMETIMES);
	content.getChildren().add(tf);

    }

    private static void addName(final HBox content, final BookingBean be) {
	final Label label = new Label(be.getGuest().getName() + "\n" + be.getBookingOrigin().getName());
	label.setWrapText(true);
	content.getChildren().add(label);
	HBox.setHgrow(label, Priority.ALWAYS);

    }

    static void addNewPayment(final String paymentString, final BookingBean be) {
	if (StringUtils.isBlank(paymentString)) {
	    return;
	}
	final double paymentAmount = Double.parseDouble(paymentString);
	final LocalDate paymentDate = LocalDate.now();
	final Payment payment = new Payment(paymentDate, paymentAmount);
	be.getPayments().add(payment);

    }

    private static void addNights(final HBox content, final BookingBean be) {
	final Text label = new Text(be.getNumberOfNights() + " nights");
	content.getChildren().add(label);
	// HBox.setHgrow(label, Priority.SOMETIMES);
    }

    private static void addRow0(final Pane content, final BookingBean be) {
	final HBox box = new HBox();
	final HBox boxName = new HBox();
	final HBox boxDates = new HBox();
	final HBox boxNights = new HBox();
	box.setAlignment(Pos.CENTER);
	boxName.getStyleClass().add("first-line");
	boxDates.getStyleClass().add("first-line");
	boxNights.getStyleClass().add("first-line");
	boxName.setPadding(boxPadding);
	boxDates.setPadding(boxPadding);
	boxNights.setPadding(boxPadding);
	boxName.setAlignment(Pos.CENTER);
	boxDates.setAlignment(Pos.CENTER);
	boxNights.setAlignment(Pos.CENTER);
	HBox.setHgrow(boxName, Priority.ALWAYS);
	HBox.setHgrow(boxDates, Priority.ALWAYS);
	HBox.setHgrow(boxNights, Priority.ALWAYS);
	addName(boxName, be);
	// box.getChildren().add(new Separator(Orientation.VERTICAL));
	addDates(boxDates, be);
	// box.getChildren().add(new Separator(Orientation.VERTICAL));
	addNights(boxNights, be);
	box.getChildren().addAll(boxName, boxDates, boxNights);
	box.getStyleClass().add(Styles.getBackgroundStyleSource(be.getBookingOrigin().getName()));
	content.getChildren().add(box);

    }

    private static void addRow2(final Pane content, final BookingBean be) {

    }

    private static void addRow4(final Pane content, final BookingBean be) {
	final HBox box = new HBox();
	box.setPadding(new Insets(4));
	box.setAlignment(Pos.CENTER_LEFT);
	box.setFillHeight(true);
	final TextFlow tf = new TextFlow();
	final Text t0 = new Text("Net Earnings: \t");
	final Text netEarnings = new Text(String.format("%3.2f", be.getNetEarnings()));
	final Text t1 = new Text("€ total \t");
	final Text netEarningsDay = new Text(String.format("%3.2f", be.getNetEarnings() / be.getNumberOfNights()));
	final Text t2 = new Text("€ /night");
	tf.getChildren().addAll(t0, netEarnings, t1, netEarningsDay, t2);
	box.getChildren().addAll(tf);
	if (be.getNetEarnings() <= 0) {
	    box.getStyleClass().addAll("warning", "warning-bg");
	}
	content.getChildren().add(box);

    }

    private final Map<BookingBean, TextInputControl> booking2CheckInNote = new HashMap<>();
    private final Map<BookingBean, TextInputControl> booking2CheckOutNote = new HashMap<>();
    private final Map<BookingBean, TextInputControl> booking2SpecialRequestNote = new HashMap<>();
    private final Map<BookingBean, TextInputControl> booking2GrossEarnings = new HashMap<>();

    private final Map<BookingBean, CheckBox> booking2WelcomeMail = new HashMap<>();

    private final Map<BookingBean, CheckBox> booking2Payment = new HashMap<>();

    private final Map<BookingBean, DatePicker> booking2PaymentDate = new HashMap<>();

    @FXML
    private VBox content;

    private MainManager manager;

    private ModifyBookingDialogFactory modifyBookingDialogFactory;

    private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> update(c.getList()));

    private void addBookingEntry(final BookingBean be) {
	// System.err.println("Adding entry for " + be);
	final VBox box = new VBox(4);
	// box.setPadding(new Insets(4));
	addRow0(box, be);
	box.getChildren().add(new Separator());
	addRowNetEarnings(box, be);
	box.getChildren().add(new Separator());
	addRowFees(box, be);
	box.getChildren().add(new Separator());
	addRow4(box, be);
	box.getChildren().add(new Separator());
	addRow5(box, be);
	box.getChildren().add(new Separator());
	addRow1(box, be);
	addRow2(box, be);
	addModifyButton(box, be);
	content.getChildren().add(box);

    }

    private void addCheckInNote(final Pane content, final BookingBean be) {
	final VBox b = new VBox();
	b.getChildren().add(new Text("Check-in Note"));
	final TextArea ta0 = new TextArea(be.getCheckInNote());
	ta0.setWrapText(true);
	ta0.setPrefHeight(80);
	b.getChildren().add(ta0);
	booking2CheckInNote.put(be, ta0);
	content.getChildren().add(b);

    }

    private void addCheckOutNote(final Pane content, final BookingBean be) {
	final VBox b = new VBox();
	b.getChildren().add(new Text("Check-out Note"));
	final TextArea ta0 = new TextArea(be.getCheckOutNote());
	ta0.setWrapText(true);
	ta0.setPrefHeight(80);
	b.getChildren().add(ta0);
	booking2CheckOutNote.put(be, ta0);
	content.getChildren().add(b);

    }

    private void addModifyButton(final VBox box, final BookingBean be) {
	final Button b = new Button();
	b.setText("Modify");
	b.setPrefWidth(100);
	b.setOnAction(e -> {
	    if (modifyBookingDialogFactory == null) {
		modifyBookingDialogFactory = new ModifyBookingDialogFactory(getManager());
	    }
	    modifyBookingDialogFactory.showDialog();
	});
	box.getChildren().add(b);

    }

    private void addRow1(final Pane content, final BookingBean be) {
	final VBox box = new VBox();
	final HBox box0 = new HBox();
	final HBox box1 = new HBox();
	final HBox box2 = new HBox();
	box.setFillWidth(true);
	box0.setFillHeight(true);
	box1.setFillHeight(true);
	box2.setFillHeight(true);
	addCheckInNote(box0, be);
	addCheckOutNote(box1, be);
	addSpecialRequestNote(box2, be);
	box.getChildren().addAll(box0, box1, box2);
	final TitledPane pane = new TitledPane("Notes", box);
	pane.setExpanded(false);
	content.getChildren().add(pane);

    }

    private void addRow5(final Pane content, final BookingBean be) {
	final HBox box = new HBox();
	box.setPadding(new Insets(4));
	box.setFillHeight(true);
	final Text text = new Text("Welcome Mail sent: ");
	final CheckBox checkBox = new CheckBox();
	checkBox.setSelected(be.isWelcomeMailSend());
	booking2WelcomeMail.put(be, checkBox);
	final Text t1 = new Text(" \tPayment done: ");

	final CheckBox cb1 = new CheckBox();
	cb1.setSelected(be.isPaymentDone());

	// if (logger.isDebugEnabled()) {
	// logger.debug("DateOfPayment for " + be + "(" + be.hashCode() + ") is " +
	// be.getDateOfPayment());
	// }

	final DatePicker dp = new DatePicker();
	dp.setValue(be.getDateOfPayment());

	dp.setPrefWidth(140);
	booking2PaymentDate.put(be, dp);

	booking2Payment.put(be, cb1);
	final TextFlow tf = new TextFlow();
	tf.getChildren().addAll(text, checkBox, t1, cb1, dp);
	box.getChildren().add(tf);
	if (!be.isWelcomeMailSend() || !be.isPaymentDone()) {
	    box.getStyleClass().addAll("warning", "warning-bg");
	} else {
	    box.getStyleClass().removeAll("warning", "warning-bg");
	}

	final HBox box2 = new HBox();
	box2.setPadding(new Insets(4));
	box2.setFillHeight(true);
	final TextField newPayment = new TextField();
	final Button addNewPaymentButton = new Button("Add payment");
	addNewPaymentButton.setOnAction(e -> {
	    addNewPayment(newPayment.getText(), be);
	});
	box2.getChildren().addAll(newPayment, addNewPaymentButton);

	content.getChildren().addAll(box, box2);

    }

    private void addRowFees(final Pane content, final BookingBean be) {
	final HBox box = new HBox();

	// configure box
	box.setSpacing(8);
	box.setPadding(boxPadding);
	box.setAlignment(Pos.CENTER);
	box.setFillHeight(true);

	// add cleaning fees
	final TextField cleaningFeesTextField = new TextField();
	Bindings.bindBidirectional(cleaningFeesTextField.textProperty(), be.cleaningFeesProperty(),
		new NumberStringConverter(decimalFormat));
	cleaningFeesTextField.setPrefWidth(prefTextInputFieldWidth);
	final TextFlow cleaningFeesTextFlow = new TextFlow(new Text("Cleaning Fees: "), cleaningFeesTextField,
		new Text(" €"));
	box.getChildren().add(cleaningFeesTextFlow);

	// add cleaning costs

	// final CleaningEntry ce = be.getCleaning();
	// if (ce != null) {
	// final TextField cleaningCostsTextField = new TextField();
	// Bindings.bindBidirectional(cleaningCostsTextField.textProperty(),
	// ce.cleaningCostsProperty(),
	// new NumberStringConverter(decimalFormat));
	// cleaningCostsTextField.setPrefWidth(prefTextInputFieldWidth);
	// final TextFlow cleaningCostsTextFlow = new TextFlow(new Text("Cleaning Costs:
	// "), cleaningCostsTextField,
	// new Text(" €"));
	// box.getChildren().add(cleaningCostsTextFlow);
	// } else {
	// final TextField cleaningCostsTextField = new TextField("No Cleaning");
	// cleaningCostsTextField.setEditable(false);
	// cleaningCostsTextField.setPrefWidth(prefTextInputFieldWidth);
	// final TextFlow cleaningCostsTextFlow = new TextFlow(new Text("Cleaning Costs:
	// "), cleaningCostsTextField);
	// cleaningCostsTextField.getStyleClass().add("warning");
	// box.getChildren().add(cleaningCostsTextFlow);
	// }
	System.err.println("Removed cleaning");

	// add service fees
	final TextField serviceFeesTextField = new TextField();
	Bindings.bindBidirectional(serviceFeesTextField.textProperty(), be.serviceFeeProperty(),
		new NumberStringConverter(decimalFormat));
	serviceFeesTextField.setPrefWidth(prefTextInputFieldWidth);
	final TextFlow serviceFeesAbsTextFlow = new TextFlow(new Text("Service Fees: "), serviceFeesTextField,
		new Text(" €"));
	box.getChildren().add(serviceFeesAbsTextFlow);

	// add service fees percent
	final TextField serviceFeesPercentTextField = new TextField();
	Bindings.bindBidirectional(serviceFeesPercentTextField.textProperty(), be.serviceFeesPercentProperty(),
		new NumberStringConverter(decimalFormat));
	serviceFeesPercentTextField.setPrefWidth(prefTextInputFieldWidth);
	final TextFlow serviceFeesPercentTextFlow = new TextFlow(new Text("Service Fees: "),
		serviceFeesPercentTextField, new Text(" %"));

	box.getChildren().add(serviceFeesPercentTextFlow);

	// add box to parent
	content.getChildren().add(box);

    }

    private void addRowNetEarnings(final Pane content, final BookingBean be) {
	final HBox box = new HBox();
	box.setSpacing(boxSpacing);
	box.setPadding(boxPadding);
	box.setAlignment(Pos.CENTER_LEFT);
	box.setFillHeight(true);
	final TextField grossEarningsExpression = new TextField(be.getGrossEarningsExpression());
	grossEarningsExpression.setPrefWidth(prefTextInputFieldWidth * 1.5);
	booking2GrossEarnings.put(be, grossEarningsExpression);
	final Text grossEarnings = new Text(decimalFormat.format(be.getGrossEarnings()));
	final TextFlow tf = new TextFlow(new Text("Gross Earnings: "), grossEarningsExpression, new Text(" = "),
		grossEarnings, new Text("€"));
	box.getChildren().addAll(tf);
	if (be.getGrossEarnings() <= 0) {
	    box.getStyleClass().addAll("warning", "warning-bg");
	}

	final HBox box2 = new HBox();
	box2.setSpacing(boxSpacing);
	box2.setPadding(boxPadding);
	box2.setAlignment(Pos.CENTER_LEFT);
	box2.setFillHeight(true);

	final Text text = new Text("Amount received: ");
	final TextField textField = new TextField();
	textField.setText(new NumberStringConverter().toString(be.getPaymentSoFar()));
	textField.setEditable(false);
	be.paymentSoFarProperty().addListener((c, o, n) -> {
	    textField.setText(new NumberStringConverter().toString(n));
	});
	box2.getChildren().addAll(text, textField);
	content.getChildren().addAll(box, box2);

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

    private void addSpecialRequestNote(final Pane content, final BookingBean be) {
	final VBox b = new VBox();
	b.getChildren().add(new Text("Special Requests"));
	final TextArea ta0 = new TextArea(be.getSpecialRequestNote());
	ta0.setWrapText(true);
	ta0.setPrefHeight(80);
	b.getChildren().add(ta0);
	booking2SpecialRequestNote.put(be, ta0);
	content.getChildren().add(b);

    }

    private void clearAll() {
	booking2CheckInNote.clear();
	booking2CheckOutNote.clear();
	booking2SpecialRequestNote.clear();
	booking2GrossEarnings.clear();
	booking2Payment.clear();
	booking2WelcomeMail.clear();
	content.getChildren().clear();
    }

    public MainManager getManager() {
	return manager;
    }

    @FXML
    public void handleActionSaveBookingDetails(final ActionEvent e) {
	for (final Entry<BookingBean, TextInputControl> en : booking2CheckInNote.entrySet()) {
	    en.getKey().setCheckInNote(en.getValue().getText());
	}
	for (final Entry<BookingBean, TextInputControl> en : booking2CheckOutNote.entrySet()) {
	    en.getKey().setCheckOutNote(en.getValue().getText());
	}
	for (final Entry<BookingBean, TextInputControl> en : booking2SpecialRequestNote.entrySet()) {
	    en.getKey().setSpecialRequestNote(en.getValue().getText());
	}
	for (final Entry<BookingBean, TextInputControl> en : booking2GrossEarnings.entrySet()) {
	    en.getKey().setGrossEarningsExpression(en.getValue().getText());
	}

	for (final Entry<BookingBean, CheckBox> en : booking2WelcomeMail.entrySet()) {
	    en.getKey().setWelcomeMailSend(en.getValue().isSelected());
	}
	// first date, then flag! Wont work otherwise
	for (final Entry<BookingBean, DatePicker> en : booking2PaymentDate.entrySet()) {
	    en.getKey().setDateOfPayment(en.getValue().getValue());
	}
	for (final Entry<BookingBean, CheckBox> en : booking2Payment.entrySet()) {
	    en.getKey().setPaymentDone(en.getValue().isSelected());
	}
	// final Stage stage = (Stage) content.getScene().getWindow();
	// stage.close();
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	RoomBeanSelectionManager.getInstance().selectionProperty().addListener(roomListener);
	SettingsManager.getInstance().cleaningFeesProperty().addListener(
		(observable, oldValue, newValue) -> update(RoomBeanSelectionManager.getInstance().getSelection()));
	update(RoomBeanSelectionManager.getInstance().getSelection());

    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
    }

    public void shutDown() {

    }

    private void update(final Collection<? extends RoomBean> rooms) {
	clearAll();
	final List<BookingBean> bookings = new ArrayList<>(
		rooms.stream().flatMap(r -> r.getFilteredBookingEntry().toStream().map(b -> b.getElement()))
			.collect(Collectors.toSet()));
	Collections.sort(bookings);
	for (final Iterator<BookingBean> it = bookings.iterator(); it.hasNext();) {
	    final BookingBean be = it.next();
	    addBookingEntry(be);
	    if (it.hasNext()) {
		addSeparator();
	    }
	}
    }

}

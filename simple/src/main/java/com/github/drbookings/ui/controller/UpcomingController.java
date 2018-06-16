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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.BookingEntry;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.CleaningEntry;
import com.github.drbookings.ui.beans.DateBean;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UpcomingController implements Initializable, ListChangeListener<DateBean> {

    private static class CheckInOutDetails implements Comparable<CheckInOutDetails> {

	String room;
	String bookingOrigin;
	String notes;

	public CheckInOutDetails(final String room, final String bookingOrigin, final String notes) {
	    this.room = room;
	    if (StringUtils.isBlank(bookingOrigin)) {
		this.bookingOrigin = "n/a";
	    } else {
		this.bookingOrigin = bookingOrigin;
	    }
	    this.notes = notes;
	}

	@Override
	public int compareTo(final CheckInOutDetails o) {
	    return room.compareTo(o.room);
	}
    }

    private final static Logger logger = LoggerFactory.getLogger(UpcomingController.class);

    private static void addCheckInNotes(final LocalDate date, final VBox box,
	    final List<CheckInOutDetails> checkInNotes) {

	if (checkInNotes.size() > 0) {
	    for (final CheckInOutDetails next : checkInNotes) {
		final TextFlow tf = new TextFlow();
		final Text t0 = new Text("Room " + next.room);
		t0.getStyleClass().add("emphasis");
		final Text t1 = new Text(" (" + next.bookingOrigin + ")");
		tf.getChildren().addAll(t0, t1);
		if (!StringUtils.isBlank(next.notes)) {
		    final Text t2 = new Text(": " + next.notes);
		    t2.getStyleClass().add("guest-message");
		    tf.getChildren().add(t2);
		}
		box.getChildren().add(tf);
	    }
	    box.getChildren().add(new Separator());
	}
    }

    private static void addCheckInSummary(final LocalDate date, final VBox box,
	    final List<CheckInOutDetails> checkInNotes) {
	final TextFlow tf = new TextFlow();
	if (checkInNotes.size() > 0) {
	    final Text t1 = new Text(checkInNotes.size() + " ");
	    t1.getStyleClass().add("emphasis");
	    t1.getStyleClass().add("copyable-label");
	    final Text t2;
	    if (checkInNotes.size() > 1) {
		t2 = new Text("check-ins,");
	    } else {
		t2 = new Text("check-in,");
	    }
	    t2.getStyleClass().add("emphasis");
	    tf.getStyleClass().add("copyable-label");
	    tf.getChildren().addAll(t1, t2);
	}

	if (checkInNotes.isEmpty()) {
	    // tf.getChildren().add(new Text("no check-ins,"));
	}
	box.getChildren().add(tf);
    }

    private static void addCheckOutNotes(final LocalDate date, final VBox box,
	    final List<CheckInOutDetails> checkOutNotes) {
	if (checkOutNotes.size() > 0) {
	    for (final CheckInOutDetails next : checkOutNotes) {
		final TextFlow tf = new TextFlow();
		final Text t0 = new Text("Room " + next.room);
		t0.getStyleClass().add("emphasis");
		final Text t1 = new Text(" (" + next.bookingOrigin + ")");
		tf.getChildren().addAll(t0, t1);
		if (!StringUtils.isBlank(next.notes)) {
		    final Text t2 = new Text(": " + next.notes);
		    t2.getStyleClass().add("guest-message");
		    tf.getChildren().add(t2);
		}
		box.getChildren().add(tf);
	    }
	    box.getChildren().add(new Separator());
	}
    }

    private static void addCheckOutSummary(final LocalDate date, final VBox box,
	    final List<CheckInOutDetails> checkOutNotes) {
	final TextFlow tf = new TextFlow();

	if (checkOutNotes.size() > 0) {
	    final Text t0;
	    t0 = new Text("");
	    final Text t1 = new Text(checkOutNotes.size() + " ");
	    t1.getStyleClass().add("emphasis");
	    final Text t2;
	    if (checkOutNotes.size() > 1) {
		t2 = new Text("check-outs,");
	    } else {
		t2 = new Text("check-out,");
	    }
	    t2.getStyleClass().add("emphasis");

	    tf.getChildren().addAll(t0, t1, t2);
	}
	if (checkOutNotes.isEmpty()) {
	    // tf.getChildren().add(new Text("no check-outs,"));
	}
	box.getChildren().add(tf);
    }

    private static void addCleanings(final LocalDate date, final VBox box,
	    final Collection<CleaningEntry> upcomingBookings) {

	for (final CleaningEntry c : upcomingBookings) {
	    final TextFlow tf = new TextFlow();
	    final Text t0 = new Text("Room " + c.getRoom() + ": ");
	    t0.getStyleClass().add("emphasis");
	    final Text t1 = new Text(c.getName());
	    tf.getChildren().addAll(t0, t1);
	    box.getChildren().add(tf);
	}

    }

    private static void addCleaningSummary(final LocalDate date, final VBox box,
	    final Collection<CleaningEntry> upcomingBookings) {
	final TextFlow tf = new TextFlow();
	if (upcomingBookings.isEmpty()) {
	    // final Text t0 = new Text("and no cleaning.");
	    // tf.getChildren().add(t0);
	} else {
	    final Text t0 = new Text("and ");
	    final Text t1 = new Text(upcomingBookings.size() + " ");
	    t1.getStyleClass().add("emphasis");
	    final Text t2 = new Text(" cleaning" + (upcomingBookings.size() > 1 ? "s." : "."));
	    t2.getStyleClass().add("emphasis");
	    tf.getChildren().addAll(t0, t1, t2);
	}
	box.getChildren().add(tf);

    }

    private static void addGeneralSummary(final LocalDate date, final VBox box,
	    final List<CheckInOutDetails> checkInNotes) {
	final Text t0 = new Text(getDateString(date));
	final Text t1 = new Text(", there " + (checkInNotes.size() > 1 ? " are " : "is "));
	t0.getStyleClass().add("emphasis");
	final TextFlow tf = new TextFlow();
	tf.getChildren().addAll(t0, t1);
	box.getChildren().add(tf);
    }

    private static String getDateString(final LocalDate date) {
	if (LocalDate.now().equals(date)) {
	    return "Today (" + date.toString() + ")";
	} else if (LocalDate.now().plusDays(1).equals(date)) {
	    return "Tomorrow (" + date.toString() + ")";
	} else if (LocalDate.now().plusDays(2).equals(date) || LocalDate.now().plusDays(3).equals(date)
		|| LocalDate.now().plusDays(4).equals(date)) {
	    return "on " + date.getDayOfWeek().toString().charAt(0)
		    + date.getDayOfWeek().toString().substring(1).toLowerCase() + " (" + date.toString() + ")";
	} else {
	    return "on " + date;
	}
    }

    @FXML
    private VBox box;

    private MainManager manager;

    private void addEvents(final LocalDate date, final Collection<BookingEntry> upcomingBookings,
	    final Collection<CleaningEntry> upcomingCleanings) {
	final VBox box = new VBox(4);
	if (date.equals(LocalDate.now())) {
	    box.getStyleClass().add("first-day");
	} else if (date.equals(LocalDate.now().plusDays(1))) {
	    box.getStyleClass().add("second-day");
	} else if (date.isAfter(LocalDate.now().plusDays(1))) {
	    box.getStyleClass().add("later");
	}

	if (upcomingBookings.stream().filter(b -> b.isCheckIn() || b.isCheckOut()).collect(Collectors.toList())
		.isEmpty() && upcomingCleanings.isEmpty()) {
	    final Text t0 = new Text(getDateString(date));
	    final Text t1 = new Text(" there are no events.");
	    t0.getStyleClass().add("emphasis");
	    final TextFlow tf = new TextFlow();
	    tf.getChildren().addAll(t0, t1);
	    box.getChildren().addAll(tf);
	} else {
	    final List<CheckInOutDetails> checkInNotes = Collections.synchronizedList(new ArrayList<>());
	    final List<CheckInOutDetails> checkOutNotes = Collections.synchronizedList(new ArrayList<>());
	    upcomingBookings.forEach(b -> {
		if (b.isCheckIn()) {
		    String note = "";
		    if (b.getElement().getCheckInNote() != null) {
			note = b.getElement().getCheckInNote();
		    }
		    if (b.getElement().getSpecialRequestNote() != null) {
			note = note + "\n" + b.getElement().getSpecialRequestNote();
		    }
		    checkInNotes.add(new CheckInOutDetails(b.getRoom().getName(),
			    b.getElement().getBookingOrigin().getName(), note));
		} else if (b.isCheckOut()) {
		    checkOutNotes.add(new CheckInOutDetails(b.getRoom().getName(),
			    b.getElement().getBookingOrigin().getName(), b.getElement().getCheckOutNote()));
		}
	    });
	    Collections.sort(checkInNotes);
	    Collections.sort(checkOutNotes);
	    addGeneralSummary(date, box, checkInNotes);
	    addCheckOutSummary(date, box, checkOutNotes);
	    addCheckOutNotes(date, box, checkOutNotes);
	    addCheckInSummary(date, box, checkInNotes);
	    addCheckInNotes(date, box, checkInNotes);
	    addCleaningSummary(date, box, upcomingCleanings);
	    addCleanings(date, box, upcomingCleanings);
	}

	this.box.getChildren().add(box);

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    @Override
    public void onChanged(final ListChangeListener.Change<? extends DateBean> c) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Updating");
	// }
	update();
    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
	// performance kill
	// manager.getUIData().addListener(this);
	update();
    }

    private void update() {
	final int lookAheadDays = SettingsManager.getInstance().getUpcomingLookAhead();
	this.box.getChildren().clear();
	for (int i = 0; i < lookAheadDays; i++) {
	    final LocalDate date = LocalDate.now().plusDays(i);
	    final List<BookingEntry> upcomingBookings = manager.getBookingEntries().stream()
		    .filter(b -> b.getDate().equals(date)).collect(Collectors.toList());
	    final List<CleaningEntry> upcomingCleanings = manager.getCleaningEntries().stream()
		    .filter(c -> c.getDate().equals(date)).collect(Collectors.toList());
	    addEvents(date, upcomingBookings, upcomingCleanings);
	    if (i != (lookAheadDays - 1)) {
		this.box.getChildren().add(new Separator());
	    }
	}
    }
}

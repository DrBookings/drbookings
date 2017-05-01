package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.beans.DateBean;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UpcomingController implements Initializable, ListChangeListener<DateBean> {

    private final static Logger logger = LoggerFactory.getLogger(UpcomingController.class);

    @FXML
    private VBox box;

    private MainManager manager;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
	manager.getUIData().addListener(this);
	update();
    }

    @Override
    public void onChanged(final ListChangeListener.Change<? extends DateBean> c) {
	// if (logger.isDebugEnabled()) {
	// logger.debug("Updating");
	// }
	update();
    }

    private void update() {
	final int lookAheadDays = SettingsManager.getInstance().getWhatsNextLookAhead();
	this.box.getChildren().clear();
	for (int i = 0; i < lookAheadDays; i++) {
	    final LocalDate date = LocalDate.now().plusDays(i);
	    final Stream<BookingEntry> upcomingBookings = manager.getBookingEntries().stream()
		    .filter(b -> b.getDate().equals(date));
	    addEvents(date, upcomingBookings);
	    if (i != lookAheadDays - 1) {
		this.box.getChildren().add(new Separator());
	    }
	}
    }

    private void addEvents(final LocalDate date, final Stream<BookingEntry> upcomingBookings) {
	final VBox box = new VBox(4);
	if (date.equals(LocalDate.now())) {
	    box.getStyleClass().add("first-day");
	} else if (date.equals(LocalDate.now().plusDays(1))) {
	    box.getStyleClass().add("second-day");
	} else if (date.isAfter(LocalDate.now().plusDays(1))) {
	    box.getStyleClass().add("later");
	}

	final List<Pair<String, String>> checkInNotes = Collections.synchronizedList(new ArrayList<>());
	final List<Pair<String, String>> checkOutNotes = Collections.synchronizedList(new ArrayList<>());
	upcomingBookings.forEach(b -> {
	    if (b.isCheckIn()) {
		checkInNotes
			.add(new ImmutablePair<String, String>(b.getRoom().getName(), b.getElement().getCheckInNote()));
	    } else if (b.isCheckOut()) {
		checkOutNotes.add(
			new ImmutablePair<String, String>(b.getRoom().getName(), b.getElement().getCheckOutNote()));
	    }
	});
	Collections.sort(checkInNotes, (l, r) -> l.getKey().compareTo(r.getKey()));
	Collections.sort(checkOutNotes, (l, r) -> l.getKey().compareTo(r.getKey()));
	addSummaryText(date, box, checkInNotes, checkOutNotes);
	addNotesText(date, box, checkInNotes, checkOutNotes);
	this.box.getChildren().add(box);

    }

    private static void addNotesText(final LocalDate date, final VBox box,
	    final List<Pair<String, String>> checkInNotes, final List<Pair<String, String>> checkOutNotes) {
	if (checkOutNotes.size() > 0) {
	    for (final Pair<String, String> next : checkOutNotes) {
		final TextFlow tf = new TextFlow();
		final Text t0 = new Text("Room " + next.getLeft());
		t0.getStyleClass().add("emphasis");
		tf.getChildren().add(t0);
		if (!StringUtils.isBlank(next.getRight())) {
		    final Text t1 = new Text(": " + next.getRight());
		    t1.getStyleClass().add("guest-message");
		    tf.getChildren().add(t1);
		}
		box.getChildren().add(tf);
	    }
	}
	if (checkInNotes.size() > 0) {
	    if (checkOutNotes.size() > 0) {
		box.getChildren().add(new Separator());
	    }
	    for (final Pair<String, String> next : checkInNotes) {
		final TextFlow tf = new TextFlow();
		final Text t0 = new Text("Room " + next.getLeft());
		t0.getStyleClass().add("emphasis");
		tf.getChildren().add(t0);
		if (!StringUtils.isBlank(next.getRight())) {
		    final Text t1 = new Text(": " + next.getRight());
		    t1.getStyleClass().add("guest-message");
		    tf.getChildren().add(t1);
		}
		box.getChildren().add(tf);
	    }
	}
    }

    private static void addSummaryText(final LocalDate date, final VBox box,
	    final List<Pair<String, String>> checkInNotes, final List<Pair<String, String>> checkOutNotes) {
	final TextFlow tf = new TextFlow();
	if (checkInNotes.size() > 0) {
	    final Text t0 = new Text(getDateString(date) + ", there is ");
	    final Text t1 = new Text(checkInNotes.size() + " ");
	    t1.getStyleClass().add("emphasis");
	    final Text t2;
	    if (checkInNotes.size() > 1) {
		t2 = new Text("check-ins.");
	    } else {
		t2 = new Text("check-in.");
	    }

	    tf.getChildren().addAll(t0, t1, t2);
	}

	if (checkOutNotes.size() > 0) {
	    final Text t0;
	    if (checkInNotes.isEmpty()) {
		t0 = new Text(getDateString(date) + ", there is ");
	    } else {
		t0 = new Text(" Also there are ");
	    }
	    final Text t1 = new Text(checkOutNotes.size() + " ");
	    t1.getStyleClass().add("emphasis");
	    final Text t2;
	    if (checkOutNotes.size() > 1) {
		t2 = new Text("check-outs.");
	    } else {
		t2 = new Text("check-out.");
	    }

	    tf.getChildren().addAll(t0, t1, t2);
	}
	if (checkInNotes.isEmpty() && checkOutNotes.isEmpty()) {
	    tf.getChildren().add(new Text("no events " + getDateString(date)));
	}
	box.getChildren().add(tf);
    }

    private static String getDateString(final LocalDate date) {
	if (LocalDate.now().equals(date)) {
	    return "Today";
	}
	if (LocalDate.now().plusDays(1).equals(date)) {
	    return "Tomorrow";
	}
	if (LocalDate.now().plusDays(2).equals(date) || LocalDate.now().plusDays(3).equals(date)) {
	    return "on " + date.getDayOfWeek().toString().charAt(0)
		    + date.getDayOfWeek().toString().substring(1).toLowerCase();
	}
	return "on " + date;
    }
}

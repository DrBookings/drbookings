package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.BookingEntry;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class UpcomingController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(UpcomingController.class);

    private MainManager manager;

    @FXML
    private TextFlow summary;

    @FXML
    private TextFlow checkInNotes;

    @FXML
    private TextFlow checkOutNotesLabel;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {

    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
	if (logger.isDebugEnabled()) {
	    logger.debug("Doing stuff " + manager);
	}
	final Stream<BookingEntry> upcomingBookings = manager.getBookingEntries().stream()
		.filter(b -> b.getDate().equals(LocalDate.now()));
	final AtomicInteger countCheckIn = new AtomicInteger(0);
	final AtomicInteger countCheckOut = new AtomicInteger(0);
	final List<Pair<String, String>> checkInNotes = Collections.synchronizedList(new ArrayList<>());
	upcomingBookings.forEach(b -> {
	    if (b.isCheckIn()) {
		countCheckIn.incrementAndGet();
		checkInNotes
			.add(new ImmutablePair<String, String>(b.getElement().getCheckInNote(), b.getRoom().getName()));
	    } else if (b.isCheckOut()) {
		countCheckOut.incrementAndGet();
	    }
	});
	buildSummaryText(countCheckIn, countCheckOut);
	buildCheckInNotesText(checkInNotes);
    }

    private void buildCheckInNotesText(final List<Pair<String, String>> checkInNotes) {
	if (checkInNotes.size() > 0) {
	    for (final Pair<String, String> next : checkInNotes) {
		final Text t0 = new Text("Room " + next.getRight() + ": ");
		final Text t1 = new Text(next.getLeft());
		t1.getStyleClass().add("guest-message");
		this.checkInNotes.getChildren().addAll(t0, t1);
	    }

	} else {
	    this.checkInNotes.getChildren().clear();
	}
    }

    private void buildSummaryText(final AtomicInteger countCheckIn, final AtomicInteger countCheckOut) {
	final Text t0 = new Text("Today, there is ");
	final Text t1 = new Text(countCheckIn.get() + " ");
	t1.getStyleClass().add("emphasis");
	final Text t2;
	if (countCheckIn.get() > 1) {
	    t2 = new Text("check-ins.");
	} else {
	    t2 = new Text("check-in.");
	}

	summary.getChildren().addAll(t0, t1, t2);

    }

}

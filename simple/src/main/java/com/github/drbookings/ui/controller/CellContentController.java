package com.github.drbookings.ui.controller;

/*-
 * #%L
 * DrBookings
 * %%
 * Copyright (C) 2016 - 2017 Alexander Kerner
 * %%
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
 * #L%
 */

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
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class CellContentController implements Initializable {

	@SuppressWarnings("unused")
	private static Logger logger = LoggerFactory.getLogger(CellContentController.class);

	private static Node buildEntryCheckIn(final BookingEntry e) {
		final Label l = getNewLabel(e.getElement().getGuest().getName());
		l.getStyleClass().add("check-in");
		// final VBox b = new VBox(l);
		// b.setStyle(" -fx-background-color: #8fbc8f;");
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
			s = rb.getCleaningEntry().getElement().getName();
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
		// l.setMaxHeight(Double.POSITIVE_INFINITY);
		l.setPadding(new Insets(2));
		l.setAlignment(Pos.CENTER);
		// VBox.setVgrow(l, Priority.ALWAYS);
		return l;
	}

	@FXML
	private VBox guestNames0;

	@FXML
	private VBox guestNames1;

	@FXML
	private VBox cleaning;

	@FXML
	private VBox cellContainer;

	public VBox getCellContainer() {
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
		// VBox.setVgrow(guestNames0, Priority.ALWAYS);
		// VBox.setVgrow(guestNames1, Priority.ALWAYS);
		// VBox.setVgrow(cleaning, Priority.ALWAYS);
	}

	public void setCellContainer(final VBox cellContainer) {
		this.cellContainer = cellContainer;
	}

	public void setCleaning(final VBox cleaning) {
		this.cleaning = cleaning;
	}

	public void setData(final RoomBean rb) {
		if (rb == null) {
			return;
		}

		BookingEntry last = null;
		boolean cleaningToAdd = true;
		for (final BookingEntry bb : rb.getFilteredBookingEntries().stream()
				.sorted(Comparator.comparing(BookingEntry::isCheckIn)).collect(Collectors.toList())) {

			if (bb.isCheckOut()) {
				cellContainer.getChildren().add(buildEntryCheckOut(bb));
				cellContainer.setAlignment(Pos.TOP_CENTER);
			}
			if (cleaningToAdd && rb.needsCleaning()) {
				cellContainer.getChildren().add(buildEntryCleaning(rb));
				cleaningToAdd = false;
			}
			if (bb.isCheckIn()) {
				cellContainer.getChildren().add(buildEntryCheckIn(bb));
				cellContainer.setAlignment(Pos.BOTTOM_CENTER);
			}
			if (cleaningToAdd && rb.hasCleaning()) {
				cellContainer.getChildren().add(buildEntryCleaning(rb));
				cleaningToAdd = false;
			}
			if (!bb.isCheckIn() && !bb.isCheckOut()) {
				cellContainer.getChildren().add(buildEntryStay(bb));
				cellContainer.setAlignment(Pos.CENTER);
			}
			last = bb;
		}

		// if (guestNames0.getChildren().isEmpty()) {
		// guestNames0.getChildren().add(getNewLabel(null));
		// }

		// if (guestNames1.getChildren().isEmpty()) {
		// guestNames1.getChildren().add(getNewLabel(null));
		// }

		if (rb.isWarning()) {
			if (rb.hasCheckIn()) {
				cellContainer.getStyleClass().add("warning-box-top");
			} else if (rb.hasCheckOut()) {
				// cellContainer.getStyleClass().add("warning-box-bottom");
			} else {
				cellContainer.getStyleClass().add("warning-box-middle");
			}
		}
		if (last != null && !last.isCheckOut()) {
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

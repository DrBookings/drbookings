/*
 * DrBookings
 *
 * Copyright (C) 2016 - 2017 Alexander Kerner
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

import com.github.drbookings.model.data.Guest;
import com.github.drbookings.model.data.manager.MainManager;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class CellContentController implements Initializable {

    @SuppressWarnings("unused")
    private static Logger logger = LoggerFactory.getLogger(CellContentController.class);
    @FXML
    private VBox guestNames0;
    @FXML
    private VBox guestNames1;
    @FXML
    private VBox cleaning;
    @FXML
    private VBox cellContainer;

    private static Node buildEntryCheckIn(final BookingEntry e) {
        final Label l = getNewLabel(e.getElement().getGuest().getName());
        if (e.getElement().isSplitBooking()) {
            Optional<BookingEntry> next = MainManager.getInstance().getBefore(e);
            if (next.isPresent()) {
                BookingEntry be = next.get();
                if (be.getElement().getGuest().equals(e.getElement().getGuest())) {
                    // do not apply label for the same guest
                    return l;
                }
            }
        }
        l.getStyleClass().add("check-in");
        return l;
    }

    private static Node buildEntryCheckOut(final BookingEntry e) {
        final Label l = getNewLabel(e.getElement().getGuest().getName());
        if (e.getElement().isSplitBooking()) {
            Optional<BookingEntry> next = MainManager.getInstance().getAfter(e);
            if (next.isPresent()) {
                BookingEntry be = next.get();
                if (be.getElement().getGuest().equals(e.getElement().getGuest())) {
                    // do not apply label for the same guest
                    return l;
                }
            }
        }
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

    public VBox getCellContainer() {
        return cellContainer;
    }

    public void setCellContainer(final VBox cellContainer) {
        this.cellContainer = cellContainer;
    }

    public VBox getCleaning() {
        return cleaning;
    }

    public void setCleaning(final VBox cleaning) {
        this.cleaning = cleaning;
    }

    public VBox getGuestNames0() {
        return guestNames0;
    }

    public void setGuestNames0(final VBox guestNamesCheckIn) {
        this.guestNames0 = guestNamesCheckIn;
    }

    public VBox getGuestNames1() {
        return guestNames1;
    }

    public void setGuestNames1(final VBox guestNamesCheckOut) {
        this.guestNames1 = guestNamesCheckOut;
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        // VBox.setVgrow(guestNames0, Priority.ALWAYS);
        // VBox.setVgrow(guestNames1, Priority.ALWAYS);
        // VBox.setVgrow(cleaning, Priority.ALWAYS);
    }

    public void setData(final RoomBean rb) {
        if (rb == null) {
            return;
        }

        Set<Guest> guestsAdded = new LinkedHashSet<>();
        BookingEntry last = null;
        boolean cleaningToAdd = true;
        for (final BookingEntry bb : rb.getFilteredBookingEntries().stream()
                .sorted(Comparator.comparing(BookingEntry::isCheckIn)).collect(Collectors.toList())) {

            if (bb.getElement().isSplitBooking() && guestsAdded.contains(bb.getElement().getGuest())) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Skipping duplicate entry " + bb);
                }
                last = bb;

                continue;
            }

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
            guestsAdded.add(bb.getElement().getGuest());
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
        if (last != null && (!last.isCheckOut() || last.getElement().isSplitBooking())) {
            cellContainer.getStyleClass()
                    .add(Styles.getBackgroundStyleSource(last.getElement().getBookingOrigin().getName()));
        }
    }

}

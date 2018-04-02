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

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.CleaningEntry;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleaningPlanController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(CleaningPlanController.class);

    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("E\tdd.MM");

    @FXML
    private TableView<CleaningEntry> content;

    private MainManager manager;

    public MainManager getManager() {
        return manager;
    }

    @FXML
    private void handleActionCopySelected(final ActionEvent event) {
        final List<CleaningEntry> selection = content.getSelectionModel().getSelectedItems();
        final StringBuilder sb = new StringBuilder();
        final String roomNamePrefix = SettingsManager.getInstance().getRoomNamePrefix();
        for (final Iterator<CleaningEntry> it = selection.iterator(); it.hasNext(); ) {
            final CleaningEntry ce = it.next();

            sb.append(CleaningPlanController.DATE_FORMATTER.format(ce.getDate()));
            sb.append("\t");
            sb.append(roomNamePrefix);
            sb.append(ce.getRoom().getName());
//	    if (CleaningEntry.ShortTerm.YES.equals(ce.isShortTime())) {
//		sb.append("\t");
//		sb.append("bis 1600");
//	    }
            System.err.println("Removed cleaning");

            if (it.hasNext()) {
                sb.append("\n");
            }
        }
        final ClipboardContent content = new ClipboardContent();
        content.putString(sb.toString());
        Clipboard.getSystemClipboard().setContent(content);
        final Tooltip t = new Tooltip("Content copied.");
        t.setAutoHide(true);
        t.setX(this.content.getScene().getWindow().getX() + this.content.getScene().getWindow()
            .getWidth()
            - this.content.getScene().getWindow().getWidth() / 2);
        t.setY(this.content.getScene().getWindow().getY() + this.content.getScene().getWindow()
            .getHeight()
            - this.content.getScene().getWindow().getHeight() / 2);
        t.show(this.content.getScene().getWindow());

    }

    @FXML
    private void handleActionDeleteSelected(final ActionEvent event) {
        final List<CleaningEntry> selection = content.getSelectionModel().getSelectedItems();
        selection.forEach(ce -> manager.removeCleaning(ce));

    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
        content.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

    public void setManager(final MainManager manager) {
        this.manager = manager;
        content.itemsProperty()
            .bind(
                Bindings.createObjectBinding(updateTable(), manager.cleaningEntriesListProperty()));
        content.sort();
    }

    private Callable<ObservableList<CleaningEntry>> updateTable() {
        return () -> FXCollections
            .observableArrayList(manager.cleaningEntriesListProperty()
                .filtered(e -> e.getDate()
                    .isAfter(LocalDate.now()
                        .minusDays(SettingsManager.getInstance().getCleaningPlanLookBehind())))
                .sorted());
    }

}

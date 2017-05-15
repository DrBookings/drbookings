package com.github.drbookings.ui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.model.settings.SettingsManager;
import com.github.drbookings.ui.CleaningEntry;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class CleaningPlanController implements Initializable {

    public MainManager getManager() {
	return manager;
    }

    public void setManager(final MainManager manager) {
	this.manager = manager;
	content.setItems(
		manager.cleaningEntriesListProperty()
			.filtered(e -> e.getDate()
				.isAfter(LocalDate.now()
					.minusDays(SettingsManager.getInstance().getCleaningPlanLookBehind())))
			.sorted());
    }

    @FXML
    private TableView<CleaningEntry> content;

    @FXML
    private void handleActionDeleteSelected(final ActionEvent event) {
	final List<CleaningEntry> selection = content.getSelectionModel().getSelectedItems();
	selection.forEach(ce -> manager.removeCleaning(ce));

    }

    private MainManager manager;

    @FXML
    private void handleActionCopySelected(final ActionEvent event) {
	final List<CleaningEntry> selection = content.getSelectionModel().getSelectedItems();
	final StringBuilder sb = new StringBuilder();
	final String roomNamePrefix = SettingsManager.getInstance().getRoomNamePrefix();
	for (final Iterator<CleaningEntry> it = selection.iterator(); it.hasNext();) {
	    final CleaningEntry ce = it.next();

	    sb.append(ce.getDate());
	    sb.append("\t");
	    sb.append(roomNamePrefix);
	    sb.append(ce.getRoom().getName());
	    // sb.append("\t");
	    // sb.append(ce.getElement().getName());

	    if (it.hasNext()) {
		sb.append("\n");
	    }
	}
	final ClipboardContent content = new ClipboardContent();
	content.putString(sb.toString());
	Clipboard.getSystemClipboard().setContent(content);
	final Tooltip t = new Tooltip("Content copied.");
	t.setAutoHide(true);
	t.setX(this.content.getScene().getWindow().getX() + this.content.getScene().getWindow().getWidth()
		- this.content.getScene().getWindow().getWidth() / 2);
	t.setY(this.content.getScene().getWindow().getY() + this.content.getScene().getWindow().getHeight()
		- this.content.getScene().getWindow().getHeight() / 2);
	t.show(this.content.getScene().getWindow());

    }

    private final static Logger logger = LoggerFactory.getLogger(CleaningPlanController.class);

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	content.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    }

}

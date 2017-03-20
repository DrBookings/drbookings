package com.github.drbookings.ui.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.CellSelectionManager;
import com.github.drbookings.model.Bookings;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.RoomBean;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RoomDetailsController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(RoomDetailsController.class);

    private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> updateUIRooms(c.getList()));

    @FXML
    private Button buttonSave;

    @FXML
    private Label checkOut;

    @FXML
    private Label checkIn;

    @FXML
    private TextField cleaning;

    private RoomBean room;

    @FXML
    private Label bruttoEarningsLabel;

    @FXML
    private Label checkInNoteLabel;

    @FXML
    private TextArea checkInNoteInput;

    @FXML
    private Label guestNames;

    @FXML
    private TextField bruttoEarningsInput;

    @FXML
    private void handleButtonSave(final ActionEvent event) {
	Platform.runLater(() -> {
	    updateModel();
	    final Stage stage = (Stage) buttonSave.getScene().getWindow();
	    stage.close();
	});
    }

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	updateUIRooms(CellSelectionManager.getInstance().getSelection());
	CellSelectionManager.getInstance().getSelection().addListener(roomListener);

    }

    public void shutDown() {
	CellSelectionManager.getInstance().getSelection().removeListener(roomListener);
    }

    private void updateModel() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Updating model");
	}
	updateModelCleaning();
	updateModelBruttoEarnings();
	updateModelCheckInNote();
    }

    private void updateModelBruttoEarnings() {
	if (!room.getBookings().isEmpty() || bruttoEarningsInput.getText() == null) {
	    try {
		final float result = Float.parseFloat(bruttoEarningsInput.getText());
		room.getBookings().get(0).setAllBruttoEarnings(result);
	    } catch (final NumberFormatException e) {
		UIUtils.showError("Invalid input", e.getLocalizedMessage());
	    }
	}
    }

    private void updateModelCheckInNote() {
	if (!room.getBookings().isEmpty()) {
	    room.getBookings().get(0).setAllCheckInNote(checkInNoteInput.getText().trim());
	}
    }

    private void updateModelCleaning() {
	if (cleaning.getText() != null) {
	    room.setCleaning(cleaning.getText().trim());
	}

    }

    private void updateUIBruttoEarnings() {
	if (room.getBookings().isEmpty()) {
	    bruttoEarningsLabel.setText(null);
	    bruttoEarningsInput.setText(null);
	} else {
	    final BookingBean bb = room.getBookings().get(0);
	    bruttoEarningsLabel.setText("Brutto earnings for\n" + bb.getGuestName() + "\n("
		    + bb.getNumberOfTotalNights() + " total nights)");
	    bruttoEarningsInput.setText("" + bb.getBruttoEarnings());
	}

    }

    private void updateUICheckInNote() {
	if (room.getBookings().isEmpty()) {
	    checkInNoteLabel.setText(null);
	    checkInNoteInput.setText(null);
	} else {
	    final BookingBean bb = room.getBookings().get(0);
	    checkInNoteLabel.setText("Check-in note for\n" + bb.getGuestName());
	    checkInNoteInput.setText(bb.getCheckInNote());
	}
    }

    private void updateUIRooms(final List<? extends RoomBean> list) {
	if (logger.isDebugEnabled()) {
	    logger.debug("Updating UI");
	}
	if (list.isEmpty()) {
	    return;
	}
	// Show only first entry
	room = list.get(0);
	final Set<String> guestNameView = Bookings.guestNameView(room.getBookings());
	if (guestNameView.isEmpty()) {
	    guestNames.setText(null);
	} else {
	    guestNames.setText(guestNameView.toString());
	}

	cleaning.setText(room.getCleaning());
	updateUIBruttoEarnings();
	updateUICheckInNote();

	final List<BookingBean> ci = Bookings.viewCheckIn(room.getBookings());
	final List<BookingBean> co = Bookings.viewCheckOut(room.getBookings());
	if (ci.isEmpty()) {
	    checkIn.setText("");
	} else {
	    checkIn.setText(Bookings.guestNameView(ci).toString());
	}
	if (co.isEmpty()) {
	    checkOut.setText("");
	} else {
	    checkOut.setText(Bookings.guestNameView(co).toString());
	}

    }

}
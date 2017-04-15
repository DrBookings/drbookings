package com.github.drbookings.ui.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.beans.RoomBean;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RoomDetailsController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(RoomDetailsController.class);

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,###,###,##0.00");

    private ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> updateUIRooms(c.getList()));

    @FXML
    private Button buttonSave;

    @FXML
    private Label bookings;

    @FXML
    private CheckBox paymentDoneButton;

    @FXML
    private Label nettoEarningsLabel;

    @FXML
    private TextField nettoEarningsOutput;

    @FXML
    private TextField cleaning;

    @FXML
    private CheckBox welcomeMailSendButton;

    private RoomBean room;

    private BookingEntry booking;

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
    private TextField bruttoEarningsInputExpression;

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
	roomListener = null;
    }

    private void updateModel() {
	if (logger.isDebugEnabled()) {
	    logger.debug("Updating model");
	}
	updateModelCleaning();
	updateModelBruttoEarnings();
	updateModelCheckInNote();
	updateModelWelcomeMail();
	updateModelPayment();
    }

    private void updateModelBruttoEarnings() {
	if (!getBookings().isEmpty() && bruttoEarningsInputExpression.getText() != null
		&& bruttoEarningsInputExpression.getText().trim().length() > 0) {
	    try {
		// final double result =
		// parseGrossEarnings(bruttoEarningsInputExpression.getText().trim());
		// getBookings().get(0).getElement().setGrossEarnings(result);
		getBookings().get(0).getElement()
			.setGrossEarningsExpression(bruttoEarningsInputExpression.getText().trim());
	    } catch (final NumberFormatException e) {
		UIUtils.showError("Invalid input", e.getLocalizedMessage());
	    }
	}
    }

    private void updateModelCheckInNote() {
	if (!getBookings().isEmpty()) {
	    getBookings().get(0).getElement().setCheckInNote(checkInNoteInput.getText());
	}
    }

    private void updateModelWelcomeMail() {
	if (!getBookings().isEmpty()) {
	    getBookings().get(0).getElement().setWelcomeMailSend(welcomeMailSendButton.isSelected());
	}

    }

    private void updateModelPayment() {
	if (!getBookings().isEmpty()) {
	    getBookings().get(0).getElement().setPaymentDone(paymentDoneButton.isSelected());
	}

    }

    private void updateUIBruttoEarnings() {
	if (getBookings().isEmpty()) {
	    bruttoEarningsLabel.setText(null);
	    bruttoEarningsInput.setText(null);
	} else {
	    bruttoEarningsLabel
		    .setText("Gross earnings (" + booking.getElement().getNumberOfNights() + " total nights)");
	    bruttoEarningsInput.setText("" + booking.getElement().getGrossEarnings());
	    bruttoEarningsInputExpression.setText(booking.getElement().getGrossEarningsExpression());
	}

    }

    private void updateUICheckInNote() {
	if (getBookings().isEmpty()) {
	    checkInNoteLabel.setText(null);
	    checkInNoteInput.setText(null);
	} else {
	    checkInNoteLabel.setText("Check-in note");
	    checkInNoteInput.setText(booking.getElement().getCheckInNote());
	}
    }

    private void updateUINettoEarnings() {
	if (getBookings().isEmpty()) {
	    nettoEarningsLabel.setText(null);
	    nettoEarningsOutput.setText(null);
	} else {
	    nettoEarningsLabel
		    .setText("Daily net earnings (" + booking.getElement().getNumberOfNights() + " total nights)");
	    nettoEarningsOutput.setText(decimalFormat.format(booking.getNetEarnings()));
	}

    }

    private static String buildBookingsLabelString(final RoomBean rb) {
	return rb.getDate().toString();
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
	bookings.setText(buildBookingsLabelString(room));
	// Show only first booking;
	booking = selectBooking();
	final Set<String> guestNameView = BookingEntry.guestNameView(getBookings());
	if (guestNameView.isEmpty()) {
	    guestNames.setText(null);
	} else {
	    guestNames.setText(guestNameView.toString());
	}

	if (room.getCleaningEntry() != null) {
	    cleaning.setText(room.getCleaningEntry().getElement().getName());
	} else {
	    cleaning.setText(null);
	}
	updateUIBruttoEarnings();
	updateUINettoEarnings();
	updateUICheckInNote();
	updateUIWelcomeMail();
	updateUIPayment();

    }

    private void updateModelCleaning() {
	if (cleaning.getText() != null && cleaning.getText().trim().length() > 0) {
	    room.setCleaning(cleaning.getText().trim());
	} else {
	    room.setCleaningEntry(null);
	}
    }

    private List<BookingEntry> getBookings() {
	return room.getFilteredBookingEntries();
    }

    private BookingEntry selectBooking() {
	final BookingEntry bb;
	if (getBookings().isEmpty()) {
	    return null;
	}
	if (getBookings().size() > 1 && !BookingEntry.checkInView(getBookings()).isEmpty()) {
	    bb = BookingEntry.checkInView(getBookings()).get(0);
	} else {
	    bb = getBookings().get(0);

	}
	return bb;
    }

    private void updateUIWelcomeMail() {
	if (booking == null) {
	    welcomeMailSendButton.setSelected(false);
	} else {
	    welcomeMailSendButton.setSelected(booking.getElement().isWelcomeMailSend());
	}
    }

    private void updateUIPayment() {
	if (booking == null) {
	    paymentDoneButton.setSelected(false);
	} else {
	    paymentDoneButton.setSelected(booking.getElement().isPaymentDone());
	}
    }
}

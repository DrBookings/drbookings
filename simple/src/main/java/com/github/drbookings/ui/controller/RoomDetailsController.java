package com.github.drbookings.ui.controller;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.BookingBeans;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.CellSelectionManager;

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

    private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> updateUIRooms(c.getList()));

    @FXML
    private Button buttonSave;

    @FXML
    private Label checkOut;

    @FXML
    private CheckBox paymentDoneButton;

    @FXML
    private Label nettoEarningsLabel;

    @FXML
    private TextField nettoEarningsOutput;

    @FXML
    private Label checkIn;

    @FXML
    private TextField cleaning;

    @FXML
    private CheckBox welcomeMailSendButton;

    private RoomBean room;

    private BookingBean booking;

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
	updateModelWelcomeMail();
	updateModelPayment();
    }

    private void updateModelBruttoEarnings() {
	if (!getBookings().isEmpty() || bruttoEarningsInput.getText() == null) {
	    try {
		final float result = Float.parseFloat(bruttoEarningsInput.getText());
		getBookings().get(0).setAllBruttoEarnings(result);
	    } catch (final NumberFormatException e) {
		UIUtils.showError("Invalid input", e.getLocalizedMessage());
	    }
	}
    }

    private void updateModelCheckInNote() {
	if (!getBookings().isEmpty()) {
	    getBookings().get(0).setAllCheckInNote(checkInNoteInput.getText());
	}
    }

    private void updateModelWelcomeMail() {
	if (!getBookings().isEmpty()) {
	    getBookings().get(0).setAllWelcomeMailSent(welcomeMailSendButton.isSelected());
	}

    }

    private void updateModelPayment() {
	if (!getBookings().isEmpty()) {
	    getBookings().get(0).setAllPaymentDone(paymentDoneButton.isSelected());
	}

    }

    private void updateUIBruttoEarnings() {
	if (getBookings().isEmpty()) {
	    bruttoEarningsLabel.setText(null);
	    bruttoEarningsInput.setText(null);
	} else {
	    bruttoEarningsLabel.setText("Gross earnings for " + booking.getGuestName() + " ("
		    + booking.getNumberOfTotalNights() + " total nights)");
	    bruttoEarningsInput.setText("" + booking.getGrossEarnings());
	}

    }

    private void updateUICheckInNote() {
	if (getBookings().isEmpty()) {
	    checkInNoteLabel.setText(null);
	    checkInNoteInput.setText(null);
	} else {
	    checkInNoteLabel.setText("Check-in note for " + booking.getGuestName());
	    checkInNoteInput.setText(booking.getCheckInNote());
	}
    }

    private void updateUINettoEarnings() {
	if (getBookings().isEmpty()) {
	    nettoEarningsLabel.setText(null);
	    nettoEarningsOutput.setText(null);
	} else {
	    nettoEarningsLabel.setText("Nightly net earnings for " + booking.getGuestName() + " ("
		    + booking.getNumberOfTotalNights() + " total nights)");
	    nettoEarningsOutput.setText(decimalFormat.format(booking.getNettoEarningsPerNight()));
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
	// Show only first booking;
	booking = selectBooking();
	final Set<String> guestNameView = BookingBeans.guestNameView(getBookings());
	if (guestNameView.isEmpty()) {
	    guestNames.setText(null);
	} else {
	    guestNames.setText(guestNameView.toString());
	}

	cleaning.setText(room.getCleaning());
	updateUIBruttoEarnings();
	updateUINettoEarnings();
	updateUICheckInNote();
	updateUIWelcomeMail();
	updateUIPayment();

	final List<BookingBean> ci = BookingBeans.checkInView(getBookings());
	final List<BookingBean> co = BookingBeans.checkOutView(getBookings());
	if (ci.isEmpty()) {
	    checkIn.setText("");
	} else {
	    checkIn.setText(BookingBeans.guestNameView(ci).toString());
	}
	if (co.isEmpty()) {
	    checkOut.setText("");
	} else {
	    checkOut.setText(BookingBeans.guestNameView(co).toString());
	}
    }

    private void updateModelCleaning() {
	if (cleaning.getText() != null) {
	    room.setCleaning(cleaning.getText().trim());
	}
    }

    private List<BookingBean> getBookings() {
	return room.getFilteredBookings();
    }

    private BookingBean selectBooking() {
	final BookingBean bb;
	if (getBookings().isEmpty()) {
	    return null;
	}
	if (getBookings().size() > 1 && !BookingBeans.getCheckInBookings(getBookings()).isEmpty()) {
	    bb = BookingBeans.getCheckInBookings(getBookings()).get(0);
	} else {
	    bb = getBookings().get(0);

	}
	return bb;
    }

    private void updateUIWelcomeMail() {
	if (booking == null) {
	    welcomeMailSendButton.setSelected(false);
	} else {
	    welcomeMailSendButton.setSelected(booking.isWelcomeMailSend());
	}
    }

    private void updateUIPayment() {
	if (booking == null) {
	    paymentDoneButton.setSelected(false);
	} else {
	    paymentDoneButton.setSelected(booking.isMoneyReceived());
	}
    }
}

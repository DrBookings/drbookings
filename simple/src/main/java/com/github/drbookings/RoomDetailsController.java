package com.github.drbookings;

import java.net.URL;
import java.util.Collection;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.Bookings;
import com.github.drbookings.model.DataModel;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.Styles;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class RoomDetailsController implements Initializable {

    private final static Logger logger = LoggerFactory.getLogger(RoomDetailsController.class);

    @FXML
    private TextField textFieldCleaning;

    @FXML
    private Label labelGuestName;

    @FXML
    private Label labelCheckIn;

    @FXML
    private Label labelCheckOut;

    @FXML
    private Label labelSource;

    @Override
    public void initialize(final URL location, final ResourceBundle resources) {
	update(RoomSelectionManager.getInstance().getSelection());
	RoomSelectionManager.getInstance().getSelection().addListener((ListChangeListener<RoomBean>) c -> {
	    // if (logger.isDebugEnabled()) {
	    // logger.debug("Rooms " + c.getList() + " selected");
	    // }
	    Platform.runLater(() -> update(c.getList()));

	});
    }

    public void update() {
	final RoomBean selectedRoom = RoomSelectionManager.getInstance().getSelection().get(0);
	selectedRoom.setCleaning(textFieldCleaning.getText());
	DataModel.getInstance().update(selectedRoom);
    }

    private void update(final Collection<? extends RoomBean> c) {
	if (c.isEmpty()) {
	    return;
	}
	final RoomBean room = c.iterator().next();
	final String cleaning = room.getCleaning();
	textFieldCleaning.setText(cleaning);
	boolean checkIn = false;
	boolean checkOut = false;
	for (final BookingBean bb : room.getBookings()) {
	    if (bb.isCheckIn()) {
		labelCheckIn.setText(bb.getGuestName());
		if (bb.getSource().equalsIgnoreCase("airbnb")) {
		    labelCheckIn.setStyle(Styles.getBackgroundStyleBookingSource("airbnb"));
		} else if (bb.getSource().equalsIgnoreCase("booking")) {
		    labelCheckIn.setStyle(Styles.getBackgroundStyleBookingSource("booking"));
		}
		checkIn = true;
	    }
	    if (bb.isCheckOut()) {
		labelCheckOut.setText(bb.getGuestName());
		if (bb.getSource().equalsIgnoreCase("airbnb")) {
		    labelCheckOut.setStyle(Styles.getBackgroundStyleBookingSource("airbnb"));
		} else if (bb.getSource().equalsIgnoreCase("booking")) {
		    labelCheckOut.setStyle(Styles.getBackgroundStyleBookingSource("booking"));
		}
		checkOut = true;
	    }

	}
	if (!checkIn) {
	    labelCheckIn.setText("");
	    labelCheckIn.setStyle("");
	}
	if (!checkOut) {
	    labelCheckOut.setText("");
	    labelCheckOut.setStyle("");
	}
	if (!room.getBookings().isEmpty()) {
	    labelGuestName.setText(Bookings.guestNameView(room.getBookings()).toString());
	} else {
	    labelGuestName.setText("");
	}

    }
}

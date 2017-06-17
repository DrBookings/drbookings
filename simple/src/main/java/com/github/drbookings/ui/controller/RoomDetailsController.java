package com.github.drbookings.ui.controller;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.LocalDates;
import com.github.drbookings.model.data.manager.MainManager;
import com.github.drbookings.ui.BookingEntry;
import com.github.drbookings.ui.CellSelectionManager;
import com.github.drbookings.ui.GuestNameAndBookingOriginView;
import com.github.drbookings.ui.beans.RoomBean;
import com.github.drbookings.ui.dialogs.BookingDetailsDialogFactory;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;

public class RoomDetailsController implements Initializable {

	private final static Logger logger = LoggerFactory.getLogger(RoomDetailsController.class);

	private static String buildBookingsLabelString(final RoomBean rb) {
		return "Room: " + rb.getName() + ", " + rb.getDate().toString();
	}

	private static void setTooltipTimes(final Tooltip obj) {
		try {
			final Class<?> clazz = obj.getClass().getDeclaredClasses()[0];
			final Constructor<?> constructor = clazz.getDeclaredConstructor(Duration.class, Duration.class,
					Duration.class, boolean.class);
			constructor.setAccessible(true);
			final Object tooltipBehavior = constructor.newInstance(new Duration(50), // open
					new Duration(5000), // visible
					new Duration(200), // close
					false);
			final Field fieldBehavior = obj.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			fieldBehavior.set(obj, tooltipBehavior);
		} catch (final Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}

	@FXML
	private Label bookings;

	@FXML
	private Button buttonSave;

	@FXML
	private TextField cleaningName;

	@FXML
	private Label cleaningBooking;

	@FXML
	private Label guestNames;

	private MainManager manager;

	private RoomBean room;

	private final ListChangeListener<RoomBean> roomListener = c -> Platform.runLater(() -> updateUIRooms(c.getList()));

	private String buildTooltipText(final List<BookingEntry> bookings) {
		final StringBuilder sb = new StringBuilder();
		for (final Iterator<BookingEntry> it = bookings.iterator(); it.hasNext();) {
			final BookingEntry e = it.next();
			sb.append(e.getElement().getBookingOrigin().getName());
			sb.append("\n");
			sb.append(LocalDates.getDateString(e.getElement().getCheckIn()));
			sb.append("\n");
			sb.append(LocalDates.getDateString(e.getElement().getCheckOut()));
			sb.append("\n");
			sb.append(e.getElement().getNumberOfNights() + " nights");
			if (it.hasNext()) {
				sb.append("\n");
			}
		}
		return sb.toString();
	}

	private List<BookingEntry> getBookings() {
		return room.getFilteredBookingEntries();
	}

	public MainManager getManager() {
		return manager;
	}

	@FXML
	private void handleButtonSave(final ActionEvent event) {
		Platform.runLater(() -> {
			updateModel();
			// final Stage stage = (Stage) buttonSave.getScene().getWindow();
			// stage.close();
		});
	}

	@Override
	public void initialize(final URL location, final ResourceBundle resources) {
		updateUIRooms(CellSelectionManager.getInstance().getSelection());
		CellSelectionManager.getInstance().getSelection().addListener(roomListener);

	}

	public void setManager(final MainManager manager) {
		this.manager = manager;
	}

	private void showBookingDetails() {
		Platform.runLater(() -> new BookingDetailsDialogFactory(getManager()).showDialog());
	}

	public void shutDown() {

		// CellSelectionManager.getInstance().getSelection().removeListener(roomListener);

	}

	private void updateModel() {
		if (logger.isDebugEnabled()) {
			logger.debug("Updating model");
		}
		updateModelCleaning();

	}

	private void updateModelCleaning() {
		if (cleaningName.getText() != null && cleaningName.getText().trim().length() > 0) {
			room.setCleaning(cleaningName.getText().trim());
		} else {
			room.removeCleaningEntry();
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
		bookings.setText(buildBookingsLabelString(room));

		final GuestNameAndBookingOriginView guestNameView = new GuestNameAndBookingOriginView(getBookings());

		if (guestNameView.isEmpty()) {
			guestNames.setText(null);
			guestNames.setTooltip(null);
			guestNames.setOnMouseClicked(null);
		} else {
			guestNames.setText(guestNameView.toString());
			final Tooltip tt = new Tooltip(buildTooltipText(getBookings()));
			setTooltipTimes(tt);
			tt.getStyleClass().add("booking-details-tooltip");
			guestNames.setTooltip(tt);
			guestNames.setOnMouseClicked(mouseEvent -> {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					// if (mouseEvent.getClickCount() == 2) {
					showBookingDetails();
					// }
				}
			});
		}

		if (room.getCleaningEntry() != null) {
			cleaningName.setText(room.getCleaningEntry().getElement().getName());
			cleaningBooking.setText("for " + room.getCleaningEntry().getBooking().getGuest().getName());
		} else {
			cleaningName.setText(null);
			cleaningBooking.setText(null);
		}

	}

}

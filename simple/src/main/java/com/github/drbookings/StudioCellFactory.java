package com.github.drbookings;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.drbookings.model.Rooms;
import com.github.drbookings.model.bean.BookingBean;
import com.github.drbookings.model.bean.DateBean;
import com.github.drbookings.model.bean.RoomBean;
import com.github.drbookings.ui.Styles;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class StudioCellFactory implements Callback<TableColumn<DateBean, DateBean>, TableCell<DateBean, DateBean>> {

    private static final Logger logger = LoggerFactory.getLogger(StudioCellFactory.class);

    private final String id;

    public StudioCellFactory(final String id) {
	this.id = id;
    }

    private void addBookingEntry(final BookingBean booking, final VBox box) {
	if (booking != null) {
	    final Label text = getEntryLabel();
	    text.setText(booking.getGuestName());
	    // if (booking.getSource().equalsIgnoreCase("airbnb")) {
	    // text.setStyle("-fx-background-color: rgba(255,90,95,0.3)");
	    // } else if (booking.getSource().equalsIgnoreCase("booking")) {
	    // text.setStyle("-fx-background-color: rgba(8,152,255,0.3)");
	    // }
	    box.getChildren().addAll(text);
	}

    }

    private void addCheckInEntry(final BookingBean booking, final VBox box) {
	if (booking != null) {
	    final Label label = getEntryLabel();
	    if (booking.isCheckIn()) {
		label.setText("Check-in");
		label.setStyle("-fx-background-color:tomato;");
		box.getChildren().add(label);
	    }
	}
    }

    private void addCheckOutEntry(final BookingBean booking, final VBox box) {
	if (booking != null) {
	    final Label label = getEntryLabel();
	    if (booking.isCheckOut()) {
		label.setText("Check-out");
		box.getChildren().add(label);
	    }
	}
    }

    private void addCleaningEntry(final RoomBean room, final VBox box) {
	final String cleaning = room.getCleaning();
	final Label cleaningLabel = getEntryLabel();
	if (cleaning != null && !cleaning.isEmpty()) {
	    cleaningLabel.setText("Cleaning " + cleaning);
	    cleaningLabel.setStyle("-fx-background-color:khaki");
	    box.getChildren().addAll(cleaningLabel);
	} else {

	    final boolean hasCheckOut = Rooms.hasCheckOut(room);
	    final boolean hasCleaning = Rooms.hasCleaningAfter(room);
	    if (hasCheckOut && !hasCleaning) {
		cleaningLabel.setText("No Cleaning!");
		cleaningLabel.setStyle("-fx-background-color:red;-fx-font-weight: bold;");
		box.getChildren().addAll(cleaningLabel);
	    }
	}
    }

    @Override
    public TableCell<DateBean, DateBean> call(final TableColumn<DateBean, DateBean> param) {
	return new TableCell<DateBean, DateBean>() {

	    @Override
	    protected void updateItem(final DateBean item, final boolean empty) {

		super.updateItem(item, empty);
		if (empty || this.getTableRow() == null) {
		    setText(null);
		    setStyle("");
		    setGraphic(null);
		} else {

		    setStyle("-fx-alignment: center;-fx-padding: 0 0 0 0;");

		    final VBox box = new VBox();
		    box.setSpacing(2);
		    box.setFillWidth(true);
		    box.setAlignment(Pos.CENTER);
		    box.setStyle("-fx-alignment: center");
		    final RoomBean room = item.getRoom(id);
		    final List<BookingBean> bookings = room.getBookings();
		    for (final Iterator<BookingBean> bookingIt = bookings.iterator(); bookingIt.hasNext();) {
			final BookingBean booking = bookingIt.next();
			addCheckOutEntry(booking, box);
			addCheckInEntry(booking, box);
			addBookingEntry(booking, box);
			if (booking != null) {
			    if (booking.getSource().equalsIgnoreCase("airbnb")) {
				box.setStyle(Styles.getBackgroundStyleBookingSource("airbnb"));
			    } else if (booking.getSource().equalsIgnoreCase("booking")) {
				box.setStyle(Styles.getBackgroundStyleBookingSource("booking"));
			    } else {
				box.setStyle(Styles.getBackgroundStyleBookingSource());
			    }
			}
			if (bookings.size() < 2 || bookingIt.hasNext()) {
			    addCleaningEntry(room, box);
			}
		    }
		    if (box.getChildren().isEmpty()) {
			final Label l = getEntryLabel();
			l.setText("Free");
			l.setStyle("-fx-opacity:0.3;");
			box.getChildren().add(l);
			addCleaningEntry(room, box);
		    }
		    setGraphic(box);
		}
	    }
	};
    }

    private Label getEntryLabel() {
	final Label label = new Label();
	label.setAlignment(Pos.CENTER);
	label.setMaxWidth(Double.POSITIVE_INFINITY);
	label.setMaxHeight(Double.POSITIVE_INFINITY);
	label.setPadding(new Insets(2));
	// label.setWrapText(true);
	return label;
    }

}
